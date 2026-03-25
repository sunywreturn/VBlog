package org.sang.config;

import org.sang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by sang on 2017/12/17.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/category/all").authenticated()
                        .requestMatchers("/reg").permitAll()
                        .requestMatchers("/admin/**").hasRole("超级管理员")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login_page")
                        .successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse, Authentication authentication)
                                    throws IOException, ServletException {
                                httpServletResponse.setContentType("application/json;charset=utf-8");
                                PrintWriter out = httpServletResponse.getWriter();
                                out.write("{\"status\":\"success\",\"msg\":\"登录成功\"}");
                                out.flush();
                                out.close();
                            }
                        })
                        .failureHandler(new AuthenticationFailureHandler() {
                            @Override
                            public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse, AuthenticationException e)
                                    throws IOException, ServletException {
                                httpServletResponse.setContentType("application/json;charset=utf-8");
                                PrintWriter out = httpServletResponse.getWriter();
                                out.write("{\"status\":\"error\",\"msg\":\"登录失败\"}");
                                out.flush();
                                out.close();
                            }
                        })
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.accessDeniedHandler(getAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/blogimg/**", "/index.html", "/static/**");
    }

    @Bean
    AccessDeniedHandler getAccessDeniedHandler() {
        return new AuthenticationAccessDeniedHandler();
    }
}
