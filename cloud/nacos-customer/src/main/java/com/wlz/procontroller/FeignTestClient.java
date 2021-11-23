package com.wlz.procontroller;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author WLZ
 */
@Component
@FeignClient(name = "nacos-provider")
public interface FeignTestClient {

    @GetMapping("/provider/test/{name}")
    public String gettestFeign(@PathVariable("name")String name);
}