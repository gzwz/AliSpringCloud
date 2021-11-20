/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/console/security/nacos/users/NacosUserDetailsServiceImpl.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package console.security.users;

import com.alibaba.nacos.auth.common.AuthConfigs;
import com.alibaba.nacos.config.server.auth.UserPersistService;
import com.alibaba.nacos.config.server.model.Page;
import com.alibaba.nacos.config.server.model.User;
import com.alibaba.nacos.core.utils.Loggers;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;























@Service
public class NacosUserDetailsServiceImpl
    implements UserDetailsService
{
    private Map<String, User> userMap = new ConcurrentHashMap<>();
    
    @Autowired
    private UserPersistService userPersistService;
    
    @Autowired
    private AuthConfigs authConfigs;
    
    @Scheduled(initialDelay = 5000L, fixedDelay = 15000L)
    private void reload() {
        try {
            Page<User> users = getUsersFromDatabase(1, 2147483647);
            if (users == null) {
                return;
            }
            
            Map<String, User> map = new ConcurrentHashMap<>(16);
            for (User user : users.getPageItems()) {
                map.put(user.getUsername(), user);
            }
            this.userMap = map;
        } catch (Exception e) {
            Loggers.AUTH.warn("[LOAD-USERS] load failed", e);
        } 
    }


    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userMap.get(username);
        if (!this.authConfigs.isCachingEnabled()) {
            user = this.userPersistService.findUserByUsername(username);
        }
        
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new NacosUserDetails(user);
    }
    
    public void updateUserPassword(String username, String password) {
        this.userPersistService.updateUserPassword(username, password);
    }
    
    public Page<User> getUsersFromDatabase(int pageNo, int pageSize) {
        return this.userPersistService.getUsers(pageNo, pageSize);
    }
    
    public User getUser(String username) {
        User user = this.userMap.get(username);
        if (!this.authConfigs.isCachingEnabled() || user == null) {
            user = getUserFromDatabase(username);
        }
        return user;
    }
    
    public User getUserFromDatabase(String username) {
        return this.userPersistService.findUserByUsername(username);
    }
    
    public List<String> findUserLikeUsername(String username) {
        return this.userPersistService.findUserLikeUsername(username);
    }
    
    public void createUser(String username, String password) {
        this.userPersistService.createUser(username, password);
    }
    
    public void deleteUser(String username) {
        this.userPersistService.deleteUser(username);
    }
}
