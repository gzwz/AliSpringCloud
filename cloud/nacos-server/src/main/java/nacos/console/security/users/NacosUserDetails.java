/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/nacos.console/security/nacos/users/NacosUserDetails.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package nacos.console.security.users;

import com.alibaba.nacos.config.server.model.User;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;






















public class NacosUserDetails
    implements UserDetails
{
    private final User user;
    
    public NacosUserDetails(User user) {
        this.user = user;
    }


    
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList("");
    }

    
    public String getPassword() {
        return this.user.getPassword();
    }

    
    public String getUsername() {
        return this.user.getUsername();
    }

    
    public boolean isAccountNonExpired() {
        return true;
    }

    
    public boolean isAccountNonLocked() {
        return true;
    }

    
    public boolean isCredentialsNonExpired() {
        return true;
    }

    
    public boolean isEnabled() {
        return true;
    }
}
