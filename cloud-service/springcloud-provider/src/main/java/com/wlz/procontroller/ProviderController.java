package com.wlz.procontroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ProviderController {

    @RequestMapping("/provider/test/{name}")
    private String providertest(@PathVariable String name){
        log.info("进入服务 springcloud-provider");
        return "服务提供方返回："+name;
    }
}
