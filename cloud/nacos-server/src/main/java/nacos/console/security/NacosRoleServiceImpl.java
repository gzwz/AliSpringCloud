/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/nacos.console/security/nacos/roles/NacosRoleServiceImpl.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package nacos.console.security;

import com.alibaba.nacos.auth.common.AuthConfigs;
import com.alibaba.nacos.auth.model.Permission;
import com.alibaba.nacos.config.server.auth.PermissionInfo;
import com.alibaba.nacos.config.server.auth.PermissionPersistService;
import com.alibaba.nacos.config.server.auth.RoleInfo;
import com.alibaba.nacos.config.server.auth.RolePersistService;
import com.alibaba.nacos.config.server.model.Page;
import com.alibaba.nacos.console.security.nacos.users.NacosUserDetailsServiceImpl;
import com.alibaba.nacos.core.utils.Loggers;
import io.jsonwebtoken.lang.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.apache.mina.util.ConcurrentHashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
































@Service
public class NacosRoleServiceImpl
{
    public static final String GLOBAL_ADMIN_ROLE = "ROLE_ADMIN";
    private static final int DEFAULT_PAGE_NO = 1;
    @Autowired
    private AuthConfigs authConfigs;
    @Autowired
    private RolePersistService rolePersistService;
    @Autowired
    private NacosUserDetailsServiceImpl userDetailsService;
    @Autowired
    private PermissionPersistService permissionPersistService;
    private volatile Set<String> roleSet = (Set<String>)new ConcurrentHashSet();
    
    private volatile Map<String, List<RoleInfo>> roleInfoMap = new ConcurrentHashMap<>();
    
    private volatile Map<String, List<PermissionInfo>> permissionInfoMap = new ConcurrentHashMap<>();

    
    @Scheduled(initialDelay = 5000L, fixedDelay = 15000L)
    private void reload() {
        try {
            Page<RoleInfo> roleInfoPage = this.rolePersistService.getRolesByUserName("", 1, 2147483647);
            if (roleInfoPage == null) {
                return;
            }
            Set<String> tmpRoleSet = new HashSet<>(16);
            Map<String, List<RoleInfo>> tmpRoleInfoMap = new ConcurrentHashMap<>(16);
            for (RoleInfo roleInfo : roleInfoPage.getPageItems()) {
                if (!tmpRoleInfoMap.containsKey(roleInfo.getUsername())) {
                    tmpRoleInfoMap.put(roleInfo.getUsername(), new ArrayList<>());
                }
                ((List<RoleInfo>)tmpRoleInfoMap.get(roleInfo.getUsername())).add(roleInfo);
                tmpRoleSet.add(roleInfo.getRole());
            } 
            
            Map<String, List<PermissionInfo>> tmpPermissionInfoMap = new ConcurrentHashMap<>(16);
            for (String role : tmpRoleSet) {
                
                Page<PermissionInfo> permissionInfoPage = this.permissionPersistService.getPermissions(role, 1, 2147483647);
                tmpPermissionInfoMap.put(role, permissionInfoPage.getPageItems());
            } 
            
            this.roleSet = tmpRoleSet;
            this.roleInfoMap = tmpRoleInfoMap;
            this.permissionInfoMap = tmpPermissionInfoMap;
        } catch (Exception e) {
            Loggers.AUTH.warn("[LOAD-ROLES] load failed", e);
        } 
    }











    
    public boolean hasPermission(String username, Permission permission) {
        if ("console/user/password".equals(permission.getResource())) {
            return true;
        }
        
        List<RoleInfo> roleInfoList = getRoles(username);
        if (Collections.isEmpty(roleInfoList)) {
            return false;
        }

        
        for (RoleInfo roleInfo : roleInfoList) {
            if ("ROLE_ADMIN".equals(roleInfo.getRole())) {
                return true;
            }
        } 

        
        if (permission.getResource().startsWith("console/")) {
            return false;
        }

        
        for (RoleInfo roleInfo : roleInfoList) {
            List<PermissionInfo> permissionInfoList = getPermissions(roleInfo.getRole());
            if (Collections.isEmpty(permissionInfoList)) {
                continue;
            }
            for (PermissionInfo permissionInfo : permissionInfoList) {
                String permissionResource = permissionInfo.getResource().replaceAll("\\*", ".*");
                String permissionAction = permissionInfo.getAction();
                if (permissionAction.contains(permission.getAction()) && 
                    Pattern.matches(permissionResource, permission.getResource())) {
                    return true;
                }
            } 
        } 
        return false;
    }
    
    public List<RoleInfo> getRoles(String username) {
        List<RoleInfo> roleInfoList = this.roleInfoMap.get(username);
        if (!this.authConfigs.isCachingEnabled() || roleInfoList == null) {
            Page<RoleInfo> roleInfoPage = getRolesFromDatabase(username, 1, 2147483647);
            if (roleInfoPage != null) {
                roleInfoList = roleInfoPage.getPageItems();
            }
        } 
        return roleInfoList;
    }
    
    public Page<RoleInfo> getRolesFromDatabase(String userName, int pageNo, int pageSize) {
        Page<RoleInfo> roles = this.rolePersistService.getRolesByUserName(userName, pageNo, pageSize);
        if (roles == null) {
            return new Page();
        }
        return roles;
    }
    
    public List<PermissionInfo> getPermissions(String role) {
        List<PermissionInfo> permissionInfoList = this.permissionInfoMap.get(role);
        if (!this.authConfigs.isCachingEnabled() || permissionInfoList == null) {
            Page<PermissionInfo> permissionInfoPage = getPermissionsFromDatabase(role, 1, 2147483647);
            if (permissionInfoPage != null) {
                permissionInfoList = permissionInfoPage.getPageItems();
            }
        } 
        return permissionInfoList;
    }
    
    public Page<PermissionInfo> getPermissionsByRoleFromDatabase(String role, int pageNo, int pageSize) {
        return this.permissionPersistService.getPermissions(role, pageNo, pageSize);
    }






    
    public void addRole(String role, String username) {
        if (this.userDetailsService.getUserFromDatabase(username) == null) {
            throw new IllegalArgumentException("user '" + username + "' not found!");
        }
        if ("ROLE_ADMIN".equals(role)) {
            throw new IllegalArgumentException("role 'ROLE_ADMIN' is not permitted to create!");
        }
        this.rolePersistService.addRole(role, username);
        this.roleSet.add(role);
    }
    
    public void deleteRole(String role, String userName) {
        this.rolePersistService.deleteRole(role, userName);
    }
    
    public void deleteRole(String role) {
        this.rolePersistService.deleteRole(role);
        this.roleSet.remove(role);
    }
    
    public Page<PermissionInfo> getPermissionsFromDatabase(String role, int pageNo, int pageSize) {
        Page<PermissionInfo> pageInfo = this.permissionPersistService.getPermissions(role, pageNo, pageSize);
        if (pageInfo == null) {
            return new Page();
        }
        return pageInfo;
    }







    
    public void addPermission(String role, String resource, String action) {
        if (!this.roleSet.contains(role)) {
            throw new IllegalArgumentException("role " + role + " not found!");
        }
        this.permissionPersistService.addPermission(role, resource, action);
    }
    
    public void deletePermission(String role, String resource, String action) {
        this.permissionPersistService.deletePermission(role, resource, action);
    }
    
    public List<String> findRolesLikeRoleName(String role) {
        return this.rolePersistService.findRolesLikeRoleName(role);
    }
}
