/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/nacos.console/controller/PermissionController.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package nacos.console.controller;

import com.alibaba.nacos.auth.annotation.Secured;
import com.alibaba.nacos.auth.common.ActionTypes;
import com.alibaba.nacos.common.model.RestResultUtils;
import com.alibaba.nacos.console.security.nacos.roles.NacosRoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


































@RestController
@RequestMapping({"/v1/auth/permissions"})
public class PermissionController
{
    @Autowired
    private NacosRoleServiceImpl nacosRoleService;
    
    @GetMapping
    @Secured(resource = "console/permissions", action = ActionTypes.READ)
    public Object getPermissions(@RequestParam int pageNo, @RequestParam int pageSize, @RequestParam(name = "role", defaultValue = "") String role) {
        return this.nacosRoleService.getPermissionsFromDatabase(role, pageNo, pageSize);
    }








    
    @PostMapping
    @Secured(resource = "console/permissions", action = ActionTypes.WRITE)
    public Object addPermission(@RequestParam String role, @RequestParam String resource, @RequestParam String action) {
        this.nacosRoleService.addPermission(role, resource, action);
        return RestResultUtils.success("add permission ok!");
    }









    
    @DeleteMapping
    @Secured(resource = "console/permissions", action = ActionTypes.WRITE)
    public Object deletePermission(@RequestParam String role, @RequestParam String resource, @RequestParam String action) {
        this.nacosRoleService.deletePermission(role, resource, action);
        return RestResultUtils.success("delete permission ok!");
    }
}
