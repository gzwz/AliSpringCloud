/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/console/controller/UserController.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package console.controller;

import com.alibaba.nacos.auth.annotation.Secured;
import com.alibaba.nacos.auth.common.ActionTypes;
import com.alibaba.nacos.auth.common.AuthConfigs;
import com.alibaba.nacos.auth.common.AuthSystemTypes;
import com.alibaba.nacos.auth.exception.AccessException;
import com.alibaba.nacos.common.model.RestResult;
import com.alibaba.nacos.common.model.RestResultUtils;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.config.server.auth.RoleInfo;
import com.alibaba.nacos.config.server.model.User;
import com.alibaba.nacos.console.security.nacos.JwtTokenManager;
import com.alibaba.nacos.console.security.nacos.NacosAuthManager;
import com.alibaba.nacos.console.security.nacos.roles.NacosRoleServiceImpl;
import com.alibaba.nacos.console.security.nacos.users.NacosUser;
import com.alibaba.nacos.console.security.nacos.users.NacosUserDetailsServiceImpl;
import com.alibaba.nacos.console.utils.PasswordEncoderUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController("user")
@RequestMapping({"/v1/auth", "/v1/auth/users"})
public class UserController
{
    @Autowired
    private JwtTokenManager jwtTokenManager;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private NacosUserDetailsServiceImpl userDetailsService;
    @Autowired
    private NacosRoleServiceImpl roleService;
    @Autowired
    private AuthConfigs authConfigs;
    @Autowired
    private NacosAuthManager authManager;
    
    @Secured(resource = "console/users", action = ActionTypes.WRITE)
    @PostMapping
    public Object createUser(@RequestParam String username, @RequestParam String password) {
        User user = this.userDetailsService.getUserFromDatabase(username);
        if (user != null) {
            throw new IllegalArgumentException("user '" + username + "' already exist!");
        }
        this.userDetailsService.createUser(username, PasswordEncoderUtil.encode(password));
        return RestResultUtils.success("create user ok!");
    }







    
    @DeleteMapping
    @Secured(resource = "console/users", action = ActionTypes.WRITE)
    public Object deleteUser(@RequestParam String username) {
        List<RoleInfo> roleInfoList = this.roleService.getRoles(username);
        if (roleInfoList != null) {
            for (RoleInfo roleInfo : roleInfoList) {
                if (roleInfo.getRole().equals("ROLE_ADMIN")) {
                    throw new IllegalArgumentException("cannot delete admin: " + username);
                }
            } 
        }
        this.userDetailsService.deleteUser(username);
        return RestResultUtils.success("delete user ok!");
    }













    
    @PutMapping
    @Secured(resource = "console/user/password", action = ActionTypes.WRITE)
    public Object updateUser(@RequestParam String username, @RequestParam String newPassword, HttpServletResponse response, HttpServletRequest request) throws IOException {
        if (!hasPermission(username, request)) {
            response.sendError(403, "authorization failed!");
        }
        
        User user = this.userDetailsService.getUserFromDatabase(username);
        if (user == null) {
            throw new IllegalArgumentException("user " + username + " not exist!");
        }
        
        this.userDetailsService.updateUserPassword(username, PasswordEncoderUtil.encode(newPassword));
        
        return RestResultUtils.success("update user ok!");
    }
    
    private boolean hasPermission(String username, HttpServletRequest request) {
        if (!this.authConfigs.isAuthEnabled()) {
            return true;
        }
        if (Objects.isNull(request.getAttribute("nacosuser"))) {
            return false;
        }
        
        NacosUser user = (NacosUser)request.getAttribute("nacosuser");
        
        if (user.isGlobalAdmin()) {
            return true;
        }
        
        return user.getUserName().equals(username);
    }








    
    @GetMapping
    @Secured(resource = "console/users", action = ActionTypes.READ)
    public Object getUsers(@RequestParam int pageNo, @RequestParam int pageSize) {
        return this.userDetailsService.getUsersFromDatabase(pageNo, pageSize);
    }














    
    @PostMapping({"/login"})
    public Object login(@RequestParam String username, @RequestParam String password, HttpServletResponse response, HttpServletRequest request) throws AccessException {
        if (AuthSystemTypes.NACOS.name().equalsIgnoreCase(this.authConfigs.getNacosAuthSystemType()) || AuthSystemTypes.LDAP
            .name().equalsIgnoreCase(this.authConfigs.getNacosAuthSystemType())) {
            NacosUser user = (NacosUser)this.authManager.login(request);
            
            response.addHeader("Authorization", "Bearer " + user.getToken());
            
            ObjectNode result = JacksonUtils.createEmptyJsonNode();
            result.put("accessToken", user.getToken());
            result.put("tokenTtl", this.authConfigs.getTokenValidityInSeconds());
            result.put("globalAdmin", user.isGlobalAdmin());
            result.put("username", user.getUserName());
            return result;
        } 

        
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);


        
        try {
            Authentication authentication = this.authenticationManager.authenticate((Authentication)authenticationToken);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String token = this.jwtTokenManager.createToken(authentication);
            
            response.addHeader("Authorization", "Bearer " + token);
            return RestResultUtils.success("Bearer " + token);
        } catch (BadCredentialsException authentication) {
            return RestResultUtils.failed(HttpStatus.UNAUTHORIZED.value(), null, "Login failed");
        } 
    }








    
    @PutMapping({"/password"})
    @Deprecated
    public RestResult<String> updatePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        User user = this.userDetailsService.getUserFromDatabase(username);
        String password = user.getPassword();

        
        try {
            if (PasswordEncoderUtil.matches(oldPassword, password).booleanValue()) {
                this.userDetailsService.updateUserPassword(username, PasswordEncoderUtil.encode(newPassword));
                return RestResultUtils.success("Update password success");
            } 
            return RestResultUtils.failed(HttpStatus.UNAUTHORIZED.value(), "Old password is invalid");
        } catch (Exception e) {
            return RestResultUtils.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Update userpassword failed");
        } 
    }







    
    @GetMapping({"/search"})
    @Secured(resource = "console/users", action = ActionTypes.WRITE)
    public List<String> searchUsersLikeUsername(@RequestParam String username) {
        return this.userDetailsService.findUserLikeUsername(username);
    }
}
