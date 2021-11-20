/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/nacos.console/controller/ServerStateController.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package nacos.console.controller;

import com.alibaba.nacos.common.utils.VersionUtils;
import com.alibaba.nacos.sys.env.EnvUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




























@RestController
@RequestMapping({"/v1/console/server"})
public class ServerStateController
{
    @GetMapping({"/state"})
    public ResponseEntity<Map<String, String>> serverState() {
        Map<String, String> serverState = new HashMap<>(4);
        serverState.put("standalone_mode", 
                EnvUtil.getStandaloneMode() ? "standalone" : "cluster");
        
        serverState.put("function_mode", EnvUtil.getFunctionMode());
        serverState.put("version", VersionUtils.version);
        
        return ResponseEntity.ok().body(serverState);
    }
}
