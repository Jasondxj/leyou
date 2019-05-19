package com.leyou.search.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private GoodsRepository repository;
    @Autowired
    private ElasticsearchTemplate template;

    public Goods buildGoods(Spu spu) {
        Long spuId = spu.getId();
        //查询商品分类名
        List<String> names = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        //查询商品品牌
        Brand brand = brandClient.queryById(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //所有的搜索字段拼接到all中，all存入索引库，并进行分词处理，搜索时与all中的字段进行匹配查询
        String all = spu.getTitle() + StringUtils.join(names, " ") + brand.getName();

        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spuId);

        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        //存储price的集合
        TreeSet<Long> priceSet = new TreeSet<>();

        //设置存储skus的json结构的集合，用map结果转化sku对象，转化为json之后与对象结构相似（或者重新定义一个对象，存储前台要展示的数据，并把sku对象转化成自己定义的对象）
        List<Map<String, Object>> skus = new ArrayList<>();
        //从sku中取出要进行展示的字段，并将sku转换成json格式
        for (Sku sku : skuList) {
            priceSet.add(sku.getPrice());
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            //sku中有多个图片，只展示第一张
            map.put("image", StringUtils.substringBefore(sku.getImages(), ","));
            map.put("price", sku.getPrice());
            skus.add(map);
        }


        //查询规格参数，规格参数中分为通用规格参数和特有规格参数
        List<SpecParam> params = specificationClient.queryParamList(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        //查询商品详情
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);

        //获取通用规格参数
        Map<Long, String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获取特有规格参数
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });

        //定义spec对应的map
        HashMap<String, Object> map = new HashMap<>();
        //对规格进行遍历，并封装spec，其中spec的key是规格参数的名称，值是商品详情中的值
        for (SpecParam param : params) {
            //key是规格参数的名称
            String key = param.getName();
            Object value = "";

            if (param.getGeneric()) {
                //参数是通用属性，通过规格参数的ID从商品详情存储的规格参数中查出值
                value = genericSpec.get(param.getId());
                if (param.getNumeric()) {
                    //参数是数值类型，处理成段，方便后期对数值类型进行范围过滤
                    value = chooseSegment(value.toString(), param);
                }
            } else {
                //参数不是通用类型
                value = specialSpec.get(param.getId());
            }
            value = (value == null ? "其他" : value);
            //存入map
            map.put(key, value);
        }


        Goods goods = new Goods();
        goods.setId(spuId);
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(all);
        goods.setPrice(priceSet);
        goods.setSubTitle(spu.getSubTitle());
        goods.setSpecs(map);
        goods.setSkus(JsonUtils.toString(skus));
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();//4.5  4-5英寸
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest request) {
        Integer page = request.getPage() - 1;
        Integer size = request.getSize();
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));
        //分页
        queryBuilder.withPageable(PageRequest.of(page, size));
        //过滤
        QueryBuilder baseQuery = buildBasicQuery(request);
        queryBuilder.withQuery(baseQuery);
        //聚合分类和品牌
        //聚合分类
        String categoryAggName = "category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //聚合品牌
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //查询
//        Page<Goods> result = repository.search(queryBuilder.build());
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //解析结果
        //解析分页结果
        long total = result.getTotalElements();
        int totalPages = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        //解析聚合结果
        Aggregations aggs = result.getAggregations();
        List<Category> categories = parseCategoryAgg(aggs.get(categoryAggName));
        List<Brand> brands = parseBrandAgg(aggs.get(brandAggName));
        //完成规格参数的聚合
        List<Map<String, Object>> specs = null;
        if (categories != null && categories.size() == 1) {
            //分类唯一，可以聚合规格参数
            specs = buildSpecificationAgg(categories.get(0).getId(), baseQuery);
        }
        return new SearchResult(total, (long) totalPages, goodsList, categories, brands, specs);

    }

    private QueryBuilder buildBasicQuery(SearchRequest request) {
        System.out.println("调用过滤条件方法");
        //创建bool查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));
        //过滤条件
        Map<String, String> map = request.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            //处理key
            if (!"cid3".equals(key) && !"brandId".equals(key)) {
                key = "specs." + key + ".keyword";
            }
            String value=entry.getValue();
            queryBuilder.filter(QueryBuilders.termQuery(key, value));
        }
        return queryBuilder;
    }

    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder baseQuery) {
        List<Map<String, Object>> specs = new ArrayList<>();
        //查询需要聚合的规格参数
        List<SpecParam> params = specificationClient.queryParamList(null, cid, true);
        //聚合
        //加上查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(baseQuery);
        for (SpecParam param : params) {
            String name = param.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name + ".keyword"));
        }
        //获取结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //解析结果
        Aggregations aggs = result.getAggregations();
        for (SpecParam param : params) {
            String name = param.getName();
            StringTerms terms = aggs.get(name);
            List<String> options = terms.getBuckets().stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());
            //准备map
            Map<String, Object> map = new HashMap<>();
            map.put("k", name);
            map.put("options", options);
            specs.add(map);
        }
        return specs;
    }

    private List<Brand> parseBrandAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brandClient.queryByIds(ids);
            return brands;
        } catch (Exception e) {
            log.error("[查询品牌异常]", e);
            return null;
        }
    }

    private List<Category> parseCategoryAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Category> categories = categoryClient.queryCategoryByIds(ids);
            return categories;
        } catch (Exception e) {
            log.error("[查询分类异常]", e);
            return null;
        }
    }

    public void createOrUpdateIndex(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        Brand brand = brandClient.queryById(spu.getBrandId());
        //构建goods
        Goods goods = buildGoods(spu);
        //存入索引库
        repository.save(goods);
    }

    public void deleteIndex(Long spuId) {
        repository.deleteById(spuId);
    }
}
