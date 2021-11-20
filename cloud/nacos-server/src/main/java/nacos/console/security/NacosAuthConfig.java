/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/nacos.console/security/nacos/NacosAuthConfig.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package nacos.console.security;

import com.alibaba.nacos.auth.common.AuthConfigs;
import com.alibaba.nacos.auth.common.AuthSystemTypes;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.console.filter.JwtAuthenticationTokenFilter;
import com.alibaba.nacos.console.security.nacos.users.NacosUserDetailsServiceImpl;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsUtils;
































@EnableGlobalMethodSecurity(prePostEnabled = true)
public class NacosAuthConfig
    extends WebSecurityConfigurerAdapter
{
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String SECURITY_IGNORE_URLS_SPILT_CHAR = ",";
    public static final String LOGIN_ENTRY_POINT = "/v1/auth/login";
    public static final String TOKEN_BASED_AUTH_ENTRY_POINT = "/v1/auth/**";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String CONSOLE_RESOURCE_NAME_PREFIX = "console/";
    public static final String UPDATE_PASSWORD_ENTRY_POINT = "console/user/password";
    private static final String DEFAULT_ALL_PATH_PATTERN = "/**";
    private static final String PROPERTY_IGNORE_URLS = "nacos.security.ignore.urls";
    @Autowired
    private Environment env;
    @Autowired
    private JwtTokenManager tokenProvider;
    @Autowired
    private AuthConfigs authConfigs;
    @Autowired
    private NacosUserDetailsServiceImpl userDetailsService;
    @Autowired
    private LdapAuthenticationProvider ldapAuthenticationProvider;
    
    @Bean(name = {"org.springframework.security.authenticationManager"})
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    
    public void configure(WebSecurity web) {
        String ignoreUrls = null;
        if (AuthSystemTypes.NACOS.name().equalsIgnoreCase(this.authConfigs.getNacosAuthSystemType())) {
            ignoreUrls = "/**";
        } else if (AuthSystemTypes.LDAP.name().equalsIgnoreCase(this.authConfigs.getNacosAuthSystemType())) {
            ignoreUrls = "/**";
        } 
        if (StringUtils.isBlank(this.authConfigs.getNacosAuthSystemType())) {
            ignoreUrls = this.env.getProperty("nacos.security.ignore.urls", "/**");
        }
        if (StringUtils.isNotBlank(ignoreUrls)) {
            for (String each : ignoreUrls.trim().split(",")) {
                web.ignoring().antMatchers(new String[] { each.trim() });
            } 
        }
    }

    
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (AuthSystemTypes.NACOS.name().equalsIgnoreCase(this.authConfigs.getNacosAuthSystemType())) {
            auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
        } else if (AuthSystemTypes.LDAP.name().equalsIgnoreCase(this.authConfigs.getNacosAuthSystemType())) {
            auth.authenticationProvider(this.ldapAuthenticationProvider);
        } 
    }


    
    protected void configure(HttpSecurity http) throws Exception {
        if (StringUtils.isBlank(this.authConfigs.getNacosAuthSystemType())) {
            ((HttpSecurity)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((HttpSecurity)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((HttpSecurity)((HttpSecurity)((HttpSecurity)http.csrf().disable()).cors()
                .and()).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()).authorizeRequests().requestMatchers(new RequestMatcher[] { CorsUtils::isPreFlightRequest })).permitAll()
                .antMatchers(new String[] { "/v1/auth/login" })).permitAll()
                .and()).authorizeRequests().antMatchers(new String[] { "/v1/auth/**" })).authenticated()
                .and()).exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint());
            
            http.headers().cacheControl();
            
            http.addFilterBefore((Filter)new JwtAuthenticationTokenFilter(this.tokenProvider), UsernamePasswordAuthenticationFilter.class);
        } 
    }

    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return (PasswordEncoder)new BCryptPasswordEncoder();
    }
}
