
package com.shu.hbase.config.springsecurity;

import com.shu.hbase.config.springsecurity.tokenlogin.TokenAuthenticationFilter;
import com.shu.hbase.config.springsecurity.tokenlogin.TokenAuthenticationSuccessHandler;
import com.shu.hbase.config.springsecurity.tokenlogin.TokenAuthticationProvider;
import com.shu.hbase.config.springsecurity.tokenlogin.TokenLoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private TokenLoginFilter tokenFilter;


    @Autowired
    private TokenAuthenticationSuccessHandler tokenAuthenticationSuccessHandler;

    @Autowired
    private MyAuthenticationFailHandler authenticationFailHandler;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Autowired
    public void configGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(customAuthenticationProvider()).eraseCredentials(true);
    }

    //用户名和密码登陆处理
    @Bean
    public TokenAuthticationProvider customAuthenticationProvider() {
        return new TokenAuthticationProvider();
    }


    /**
     * 添加token登陆验证的过滤器
     */

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
        TokenAuthenticationFilter filter = new TokenAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(tokenAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailHandler);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                .permitAll()
                .loginPage("/Login")

                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/Login", "/css/*", "/js/*", "/fonts/*", "/images/*").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/**").permitAll()
                .anyRequest()
                .authenticated()
                .and().rememberMe()
                .tokenValiditySeconds(60 * 60 * 24)
                .and().logout().deleteCookies("JSESSIONID");
    }

    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailService).passwordEncoder(new BCryptPasswordEncoder());
        auth.authenticationProvider(customAuthenticationProvider());
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .withUser("19721631").password(new BCryptPasswordEncoder().encode("aaa")).roles("USER");
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .withUser("19721632").password(new BCryptPasswordEncoder().encode("aaa")).roles("USER");
    }


}

