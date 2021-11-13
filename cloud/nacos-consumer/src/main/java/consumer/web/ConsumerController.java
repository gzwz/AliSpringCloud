package consumer.web;

import consumer.client.ProviderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by forezp on 2019/5/11.
 */

@RestController
public class ConsumerController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ProviderClient providerClient;

    @GetMapping("/hello-test")
    public String resttemplate(){
        return restTemplate.getForObject("http://nacos-provider/hello?name=小红",String.class);

    }

    @GetMapping("/helloFeign")
    public String helloFeign(){
       return providerClient.hello("小敏");
    }
}
