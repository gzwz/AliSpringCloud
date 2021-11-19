import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(
//    scanBasePackages = {"com.alibaba.nacos"}
//)
@SpringBootApplication
//@ServletComponentScan
//@EnableScheduling
public class NacosServerAppliction {


    public static void main(String[] args) {
        args = new String[2];
        args [0] = "-m";
        args [1] = "standalone";
        SpringApplication.run(NacosServerAppliction.class, args);
    }
}