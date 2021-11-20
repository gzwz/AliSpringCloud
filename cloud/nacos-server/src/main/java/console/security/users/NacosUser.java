/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/console/security/nacos/users/NacosUser.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package console.security.users;

import com.alibaba.nacos.auth.model.User;























public class NacosUser
    extends User
{
    private String token;
    private boolean globalAdmin = false;
    
    public String getToken() {
        return this.token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public boolean isGlobalAdmin() {
        return this.globalAdmin;
    }
    
    public void setGlobalAdmin(boolean globalAdmin) {
        this.globalAdmin = globalAdmin;
    }

    
    public String toString() {
        return "NacosUser{token='" + this.token + '\'' + ", globalAdmin=" + this.globalAdmin + '}';
    }
}
