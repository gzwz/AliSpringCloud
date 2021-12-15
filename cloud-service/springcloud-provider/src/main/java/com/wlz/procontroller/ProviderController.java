package com.wlz.procontroller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {

    @RequestMapping("/provider/test/{name}")
    private String providertest(@PathVariable String name){
        return "服务提供方返回："+name;
    }
}
