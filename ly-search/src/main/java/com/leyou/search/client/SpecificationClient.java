package com.leyou.search.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;

@Repository
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi{
}
