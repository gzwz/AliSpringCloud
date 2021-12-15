package com.wlz.gateway.events;

import com.wlz.gateway.service.IRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

/**
 * @author WLZ
 */
@Component
@Slf4j
public class BusReceiver {

    @Autowired
    private IRouteService routeService;

    public void handleMessage(RouteDefinition routeDefinition) {
        log.info("Received Message:<{}>", routeDefinition);
        // 待实现动态del路由
        routeService.save(routeDefinition);
    }
}
