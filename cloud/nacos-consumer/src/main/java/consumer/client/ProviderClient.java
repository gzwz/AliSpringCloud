package consumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by forezp on 2019/5/11.
 */
@FeignClient("nacos-provider")
public interface ProviderClient {

    @GetMapping("/hello")
    String hello(@RequestParam(value = "name", defaultValue = "小刚", required = false) String name);
}


