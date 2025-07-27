package com.neofoc.app.config;
import com.neofoc.springboot.config.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = false)
public class WebSecurityConfig {

    @Autowired
    SecurityConfiguration securityConfig;

    @Bean
    public MvcRequestMatcher.Builder mvcMatcherBuilder(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,MvcRequestMatcher.Builder mvc) throws Exception{
        // Enable CORS and disable CSRF
        httpSecurity = httpSecurity.cors(withDefaults()).csrf(csrf -> csrf.disable());

        // Set session management to stateless
        httpSecurity = httpSecurity
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        securityConfig.configure(httpSecurity, mvc);
        // httpSecurity.anonymous(an->an.disable());
        // Add JWT token filter
//        httpSecurity.addFilterBefore(gamailAuthFilter, UsernamePasswordAuthenticationFilter.class);
//        httpSecurity.addFilterAfter(profileCheckFilter, GamailAuthFilter.class);

        //ALERT: Activate that filter to enable JWT authentication
//        httpSecurity.addFilter(jwtAuthenticationFilter);
        return httpSecurity.build();
    }
}