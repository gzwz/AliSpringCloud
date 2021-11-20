/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/nacos.console/controller/HealthController.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package nacos.console.controller;

import com.alibaba.nacos.config.server.service.repository.PersistService;
import com.alibaba.nacos.naming.controllers.OperatorController;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController("consoleHealth")
@RequestMapping({"/v1/console/health"})
public class HealthController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);
    
    private final PersistService persistService;
    
    private final OperatorController apiCommands;
    
    @Autowired
    public HealthController(PersistService persistService, OperatorController apiCommands) {
        this.persistService = persistService;
        this.apiCommands = apiCommands;
    }






    
    @GetMapping({"/liveness"})
    public ResponseEntity<String> liveness() {
        return ResponseEntity.ok().body("OK");
    }






    
    @GetMapping({"/readiness"})
    public ResponseEntity<String> readiness(HttpServletRequest request) {
        boolean isConfigReadiness = isConfigReadiness();
        boolean isNamingReadiness = isNamingReadiness(request);
        
        if (isConfigReadiness && isNamingReadiness) {
            return ResponseEntity.ok().body("OK");
        }
        
        if (!isConfigReadiness && !isNamingReadiness) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Config and Naming are not in readiness");
        }
        
        if (!isConfigReadiness) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Config is not in readiness");
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Naming is not in readiness");
    }

    
    private boolean isConfigReadiness() {
        try {
            this.persistService.configInfoCount("");
            return true;
        } catch (Exception e) {
            LOGGER.error("Config health check fail.", e);
            
            return false;
        } 
    }
    private boolean isNamingReadiness(HttpServletRequest request) {
        try {
            this.apiCommands.metrics(request);
            return true;
        } catch (Exception e) {
            LOGGER.error("Naming health check fail.", e);
            
            return false;
        } 
    }
}
