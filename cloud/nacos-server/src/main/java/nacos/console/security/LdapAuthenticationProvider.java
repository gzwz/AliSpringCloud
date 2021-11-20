/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/nacos.console/security/nacos/LdapAuthenticationProvider.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package nacos.console.security;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.config.server.auth.RoleInfo;
import com.alibaba.nacos.config.server.model.User;
import com.alibaba.nacos.console.security.nacos.roles.NacosRoleServiceImpl;
import com.alibaba.nacos.console.security.nacos.users.NacosUserDetails;
import com.alibaba.nacos.console.security.nacos.users.NacosUserDetailsServiceImpl;
import com.alibaba.nacos.console.utils.PasswordEncoderUtil;
import java.util.Hashtable;
import java.util.List;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
























@Component
public class LdapAuthenticationProvider
    implements AuthenticationProvider
{
    private static final Logger LOG = LoggerFactory.getLogger(LdapAuthenticationProvider.class);
    
    private static final String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    
    private static final String TIMEOUT = "com.sun.jndi.ldap.connect.timeout";
    
    private static final String DEFAULT_PASSWORD = "nacos";
    
    private static final String LDAP_PREFIX = "LDAP_";
    
    private static final String DEFAULT_SECURITY_AUTH = "simple";
    
    @Autowired
    private NacosUserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private NacosRoleServiceImpl nacosRoleService;
    
    @Value("${nacos.core.auth.ldap.url:ldap://localhost:389}")
    private String ldapUrl;
    
    @Value("${nacos.core.auth.ldap.timeout:3000}")
    private String time;
    
    @Value("${nacos.core.auth.ldap.userdn:cn={0},ou=user,dc=company,dc=com}")
    private String userNamePattern;
    
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails;
        String username = (String)authentication.getPrincipal();
        String password = (String)authentication.getCredentials();
        
        if (isAdmin(username)) {
            userDetails = this.userDetailsService.loadUserByUsername(username);
            if (PasswordEncoderUtil.matches(password, userDetails.getPassword()).booleanValue()) {
                return (Authentication)new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
            }
            return null;
        } 

        
        if (!ldapLogin(username, password)) {
            return null;
        }

        
        try {
            userDetails = this.userDetailsService.loadUserByUsername("LDAP_" + username);
        } catch (UsernameNotFoundException exception) {
            String nacosPassword = PasswordEncoderUtil.encode("nacos");
            this.userDetailsService.createUser("LDAP_" + username, nacosPassword);
            User user = new User();
            user.setUsername("LDAP_" + username);
            user.setPassword(nacosPassword);
            userDetails = new NacosUserDetails(user);
        } 
        return (Authentication)new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
    
    private boolean isAdmin(String username) {
        List<RoleInfo> roleInfos = this.nacosRoleService.getRoles(username);
        if (CollectionUtils.isEmpty(roleInfos)) {
            return false;
        }
        for (RoleInfo roleinfo : roleInfos) {
            if ("ROLE_ADMIN".equals(roleinfo.getRole())) {
                return true;
            }
        } 
        return false;
    }
    
    private boolean ldapLogin(String username, String password) throws AuthenticationException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("java.naming.provider.url", this.ldapUrl);
        env.put("java.naming.security.authentication", "simple");
        
        env.put("java.naming.security.principal", this.userNamePattern.replace("{0}", username));
        env.put("java.naming.security.credentials", password);
        env.put("com.sun.jndi.ldap.connect.timeout", this.time);
        LdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
        } catch (CommunicationException e) {
            LOG.error("LDAP Service connect timeout:{}", e.getMessage());
            throw new RuntimeException("LDAP Service connect timeout");
        } catch (AuthenticationException e) {
            LOG.error("login error:{}", e.getMessage());
            throw new RuntimeException("login error!");
        } catch (Exception e) {
            LOG.warn("Exception cause by:{}", e.getMessage());
            return false;
        } finally {
            closeContext(ctx);
        } 
        return true;
    }

    
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
    
    private void closeContext(DirContext ctx) {
        if (ctx != null)
            try {
                ctx.close();
            } catch (Exception e) {
                LOG.error("Exception closing context", e);
            }  
    }
}
