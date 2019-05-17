package com.leyou.search.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;

@Repository
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {

}
