package com.swd.backend.security;

import com.swd.backend.constance.ApiEndpointProperties;
import com.swd.backend.constance.RoleProperties;
import com.swd.backend.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    RestAccessDenyEntryPoint restAccessDenyEntryPoint;
    RestUnauthorizedEntryPoint restUnauthorizedEntryPoint;
    AccountService accountService;
    JwtTokenFilter jwtTokenFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> {
            return SecurityUserDetails.builder().username(username).password("PASSWORD").role("USER").build();
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Enable CORS
        http.cors();
        //Disable CSRF
        http.csrf().disable();

        //Enable stateless
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        //Set access denied handler
        http.exceptionHandling().accessDeniedHandler(restAccessDenyEntryPoint);
        //Set unauthorized handler
        http.exceptionHandling().authenticationEntryPoint(restUnauthorizedEntryPoint);
        //Disable form login default spring security 5.0
        http.formLogin().disable();
        //Disable login http basic
        http.httpBasic().disable();

        //Public endpoints
        http.authorizeHttpRequests().antMatchers(ApiEndpointProperties.publicEndpoint).permitAll();
        http.authorizeHttpRequests().antMatchers(ApiEndpointProperties.userOnlyEndpoint).hasAuthority(RoleProperties.ROLE_USER);
        http.authorizeHttpRequests().antMatchers(ApiEndpointProperties.ownerOnlyEndpoint).hasAuthority(RoleProperties.ROLE_OWNER);
        http.authorizeHttpRequests().antMatchers(ApiEndpointProperties.adminOnlyEndpoint).hasAuthority(RoleProperties.ROLE_ADMIN);
        //Permission endpoints
        http.authorizeHttpRequests().anyRequest().authenticated();


        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
        // Remove the ROLE_ prefix default spring security generate
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}
