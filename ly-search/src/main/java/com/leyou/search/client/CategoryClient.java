package com.leyou.search.client;

import com.leyou.item.api.Categoryapi;
import com.leyou.item.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Repository
@FeignClient("item-service")
public interface CategoryClient extends Categoryapi{

}
