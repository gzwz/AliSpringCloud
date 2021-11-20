/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/console/security/nacos/CustomAuthenticationProvider.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package console.security;

import com.alibaba.nacos.console.security.nacos.users.NacosUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;























@Component
public class CustomAuthenticationProvider
    implements AuthenticationProvider
{
    @Autowired
    private NacosUserDetailsServiceImpl userDetailsService;
    
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String)authentication.getPrincipal();
        String password = (String)authentication.getCredentials();
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        
        if (!password.equals(userDetails.getPassword())) {
            return (Authentication)new UsernamePasswordAuthenticationToken(username, null, null);
        }
        return null;
    }

    
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
