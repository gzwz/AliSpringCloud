/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/nacos.console/utils/PasswordEncoderUtil.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package nacos.console.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;






















public class PasswordEncoderUtil
{
    public static Boolean matches(String raw, String encoded) {
        return Boolean.valueOf((new BCryptPasswordEncoder()).matches(raw, encoded));
    }
    
    public static String encode(String raw) {
        return (new BCryptPasswordEncoder()).encode(raw);
    }
}
