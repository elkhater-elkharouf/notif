package com.example.userservice.Security;


import com.example.userservice.Services.User.IUserService;
import com.example.userservice.Services.User.UserDetailsServiceImp;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

 private UserDetailsServiceImp userDetailsServiceImp;
 private IUserService userService;

    public SecurityConfig(UserDetailsServiceImp userDetailsServiceImp, IUserService userService) {
        this.userDetailsServiceImp = userDetailsServiceImp;
        this.userService = userService;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //qui sont les utilisateurs qui ont les droits d'acceder
        auth.userDetailsService(userDetailsServiceImp);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {  //Spécifier les droit d'acces
        http
                .authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .antMatchers("/requestPasswordReset/**","/password-reset/**","/AllUsers").permitAll()
                .antMatchers("/login", "/refreshToken", "/activation","/addUser","updateUser").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();


        //activation du filtre
        http.addFilter(new JwtAuthentificationFilter(authenticationManagerBean(), userService));
        //second Filtre
        http.addFilterBefore(new JwtAuthorizationFilter(userService), UsernamePasswordAuthenticationFilter.class);



    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



}
