package com.masa.karma_house.configuration;

import com.masa.karma_house.security.CustomUserDetailsService;
import com.masa.karma_house.security.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    //clientId: 385216338714-ljakpfs3agbglvsrv4rro6ofej6brshh.apps.googleusercontent.com
    //clientSecret: bmxoXOofZ5VGN2x5qb5QmCKu

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JWTFilter jwtFilter;

    @Bean 
    PasswordEncoder getPasswordEncoder() {
    	return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable().authorizeRequests()
                .antMatchers("/authenticate").permitAll()
               // .and().authorizeRequests().antMatchers("/propets-app.herokuapp.com/account/**/role/**").hasAuthority("ROLE_ADMIN")
               // .and().authorizeRequests().antMatchers("/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
              //  .and().authorizeRequests().antMatchers("/admin/**").hasAuthority("ADMIN")
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

       // http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
    
    /*@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET, OPTIONS, POST, PUT, DELETE")
                .allowedHeaders("Access-Control-Allow-Headers, Content-Type, Authorization, X-Token");
    }*/
    
   
}
