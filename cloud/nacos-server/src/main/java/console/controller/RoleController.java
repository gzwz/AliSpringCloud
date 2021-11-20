/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/console/controller/RoleController.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package console.controller;

import com.alibaba.nacos.auth.annotation.Secured;
import com.alibaba.nacos.auth.common.ActionTypes;
import com.alibaba.nacos.common.model.RestResultUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.console.security.nacos.roles.NacosRoleServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


































@RestController
@RequestMapping({"/v1/auth/roles"})
public class RoleController
{
    @Autowired
    private NacosRoleServiceImpl roleService;
    
    @GetMapping
    @Secured(resource = "console/roles", action = ActionTypes.READ)
    public Object getRoles(@RequestParam int pageNo, @RequestParam int pageSize, @RequestParam(name = "username", defaultValue = "") String username) {
        return this.roleService.getRolesFromDatabase(username, pageNo, pageSize);
    }






    
    @GetMapping({"/search"})
    @Secured(resource = "console/roles", action = ActionTypes.READ)
    public List<String> searchRoles(@RequestParam String role) {
        return this.roleService.findRolesLikeRoleName(role);
    }









    
    @PostMapping
    @Secured(resource = "console/roles", action = ActionTypes.WRITE)
    public Object addRole(@RequestParam String role, @RequestParam String username) {
        this.roleService.addRole(role, username);
        return RestResultUtils.success("add role ok!");
    }








    
    @DeleteMapping
    @Secured(resource = "console/roles", action = ActionTypes.WRITE)
    public Object deleteRole(@RequestParam String role, @RequestParam(name = "username", defaultValue = "") String username) {
        if (StringUtils.isBlank(username)) {
            this.roleService.deleteRole(role);
        } else {
            this.roleService.deleteRole(role, username);
        } 
        return RestResultUtils.success("delete role of user " + username + " ok!");
    }
}
