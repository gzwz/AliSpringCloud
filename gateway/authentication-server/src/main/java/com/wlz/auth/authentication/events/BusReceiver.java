package com.wlz.auth.authentication.events;

import com.wlz.auth.authentication.service.impl.ResourceService;
import com.wlz.sysadmin.organization.entity.po.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BusReceiver {

    @Autowired
    private ResourceService resourceService;

    public void handleMessage(Resource resource) {
        log.info("Received Message:<{}>", resource);
        resourceService.saveResource(resource);
    }
}
