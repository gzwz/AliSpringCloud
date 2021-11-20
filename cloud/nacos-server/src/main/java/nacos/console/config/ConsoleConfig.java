////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package nacos.console.config;
//
//import com.alibaba.nacos.core.code.ControllerMethodsCache;
//import java.time.ZoneId;
//import javax.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.stereotype.Component;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//@Component
//@EnableScheduling
//@PropertySource({"/application.properties"})
//public class ConsoleConfig {
//    @Autowired
//    private ControllerMethodsCache methodsCache;
//
//    public ConsoleConfig() {
//    }
//
//    @PostConstruct
//    public void init() {
//        this.methodsCache.initClassMethod("com.alibaba.nacos.core.controller");
//        this.methodsCache.initClassMethod("com.alibaba.nacos.naming.controllers");
//        this.methodsCache.initClassMethod("com.alibaba.nacos.config.server.controller");
//        this.methodsCache.initClassMethod("com.alibaba.nacos.nacos.console.controller");
//    }
//
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.setMaxAge(18000L);
//        config.addAllowedMethod("*");
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
//
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
//        return (jacksonObjectMapperBuilder) -> {
//            jacksonObjectMapperBuilder.timeZone(ZoneId.systemDefault().toString());
//        };
//    }
//}
