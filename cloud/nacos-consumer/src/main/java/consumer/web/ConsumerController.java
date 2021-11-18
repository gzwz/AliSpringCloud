package consumer.web;

import consumer.client.ProviderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author W
 * @Date 2021-11-14
 */
@RestController
public class ConsumerController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ProviderClient providerClient;

    @GetMapping("/hellotemp")
    public String resttemplate(){
        String result = restTemplate.getForObject("http://nacos-provider/hello?name=小红", String.class);
        System.out.printf("返回结果："+result);
        return result;

    }

    @GetMapping("/helloFeign")
    public String helloFeign(){
       return providerClient.hello("小敏");
    }

}
