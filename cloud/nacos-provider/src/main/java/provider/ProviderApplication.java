package provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProviderApplication {

    public static void main(String[] args) {
        args = new String[2];
        args [0] = "-m";
        args [1] = "standalone";
        SpringApplication.run(ProviderApplication.class,args);
    }
}
