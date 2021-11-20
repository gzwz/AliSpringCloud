/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/console/security/nacos/NacosAuthManager.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package console.security;

import com.alibaba.nacos.api.remote.request.Request;
import com.alibaba.nacos.auth.AuthManager;
import com.alibaba.nacos.auth.exception.AccessException;
import com.alibaba.nacos.auth.model.Permission;
import com.alibaba.nacos.auth.model.User;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.config.server.auth.RoleInfo;
import com.alibaba.nacos.console.security.nacos.roles.NacosRoleServiceImpl;
import com.alibaba.nacos.console.security.nacos.users.NacosUser;
import com.alibaba.nacos.core.utils.Loggers;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;































@Component
public class NacosAuthManager
    implements AuthManager
{
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";
    @Autowired
    private JwtTokenManager tokenManager;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private NacosRoleServiceImpl roleService;
    
    public User login(Object request) throws AccessException {
        HttpServletRequest req = (HttpServletRequest)request;
        String token = resolveToken(req);
        if (StringUtils.isBlank(token)) {
            throw new AccessException("user not found!");
        }
        
        try {
            this.tokenManager.validateToken(token);
        } catch (ExpiredJwtException e) {
            throw new AccessException("token expired!");
        } catch (Exception e) {
            throw new AccessException("token invalid!");
        } 
        
        Authentication authentication = this.tokenManager.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String username = authentication.getName();
        NacosUser user = new NacosUser();
        user.setUserName(username);
        user.setToken(token);
        List<RoleInfo> roleInfoList = this.roleService.getRoles(username);
        if (roleInfoList != null) {
            for (RoleInfo roleInfo : roleInfoList) {
                if (roleInfo.getRole().equals("ROLE_ADMIN")) {
                    user.setGlobalAdmin(true);
                    break;
                } 
            } 
        }
        req.setAttribute("nacosuser", user);
        return user;
    }

    
    public User loginRemote(Object request) throws AccessException {
        Request req = (Request)request;
        String token = resolveToken(req);
        if (StringUtils.isBlank(token)) {
            throw new AccessException("user not found!");
        }
        
        try {
            this.tokenManager.validateToken(token);
        } catch (ExpiredJwtException e) {
            throw new AccessException("token expired!");
        } catch (Exception e) {
            throw new AccessException("token invalid!");
        } 
        
        Authentication authentication = this.tokenManager.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String username = authentication.getName();
        NacosUser user = new NacosUser();
        user.setUserName(username);
        user.setToken(token);
        List<RoleInfo> roleInfoList = this.roleService.getRoles(username);
        if (roleInfoList != null) {
            for (RoleInfo roleInfo : roleInfoList) {
                if (roleInfo.getRole().equals("ROLE_ADMIN")) {
                    user.setGlobalAdmin(true);
                    break;
                } 
            } 
        }
        return user;
    }

    
    public void auth(Permission permission, User user) throws AccessException {
        if (Loggers.AUTH.isDebugEnabled()) {
            Loggers.AUTH.debug("auth permission: {}, user: {}", permission, user);
        }
        
        if (!this.roleService.hasPermission(user.getUserName(), permission)) {
            throw new AccessException("authorization failed!");
        }
    }



    
    private String resolveToken(HttpServletRequest request) throws AccessException {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        bearerToken = request.getParameter("accessToken");
        if (StringUtils.isBlank(bearerToken)) {
            String userName = request.getParameter("username");
            String password = request.getParameter("password");
            bearerToken = resolveTokenFromUser(userName, password);
        } 
        
        return bearerToken;
    }



    
    private String resolveToken(Request request) throws AccessException {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        bearerToken = request.getHeader("accessToken");
        if (StringUtils.isBlank(bearerToken)) {
            String userName = request.getHeader("username");
            String password = request.getHeader("password");
            bearerToken = resolveTokenFromUser(userName, password);
        } 
        
        return bearerToken;
    }
    
    private String resolveTokenFromUser(String userName, String rawPassword) throws AccessException {
        String finalName;
        Authentication authenticate;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, rawPassword);
            
            authenticate = this.authenticationManager.authenticate((Authentication)authenticationToken);
        } catch (AuthenticationException e) {
            throw new AccessException("unknown user!");
        } 
        
        if (null == authenticate || StringUtils.isBlank(authenticate.getName())) {
            finalName = userName;
        } else {
            finalName = authenticate.getName();
        } 
        
        return this.tokenManager.createToken(finalName);
    }
}
