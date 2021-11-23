package com.wlz.procontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ClientController {

    @Autowired
    private RestTemplate restclient;

    @GetMapping("test/{name}")
    public String test(@PathVariable String name) { //使用虚拟主机名进行调用
        String forObject = restclient.getForObject("http://nacos-provider/provider/test/" + name, String.class);
        return forObject;
    }

}
