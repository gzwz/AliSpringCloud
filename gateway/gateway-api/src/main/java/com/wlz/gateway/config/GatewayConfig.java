//package gateway.config;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author WLZ
// */
//@Configuration
//public class GatewayConfig {
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder){
//        // 路由构造器
//        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();
//        // 设置路径
//        routes.route("test_routh", r -> {
//            return r.path("/get").uri("http://httpbin.org/");
//        });
//        routes.route("oauthid", ru->{
//            return ru.path("/guonei").uri("http://news.baidu.com/");
//        });
////        routes.route("baidu_news_guoji_routh", r -> {
////            return r.path("/guoji").uri("http://news.baidu.com/guoji");
////        });
//
//        return routes.build();
//    }
//}