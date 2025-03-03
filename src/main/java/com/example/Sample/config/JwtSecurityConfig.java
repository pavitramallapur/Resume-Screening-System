//package com.example.Sample.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//
//import com.example.Sample.util.JwtUtil;
//
//@EnableWebSecurity
//public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .csrf().disable()
//            .authorizeRequests()
//                .antMatchers("/api/public/**").permitAll()
//                .anyRequest().authenticated()
//            .and()
//            .addFilterBefore(new JwtAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
//    }
//}
//
