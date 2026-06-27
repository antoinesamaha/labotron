package com.neofoc.app.config;

import com.neofoc.springboot.config.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
public class SecurityConfiguration {

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList(allowedOrigin));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthenticationFilter());
        registrationBean.addUrlPatterns("/foc/obj/**");
        registrationBean.addUrlPatterns("/api/**");
        registrationBean.setOrder(1);
        registrationBean.setName("JwtAuthenticationFilter");

        // Exclude the login endpoint
        registrationBean.addInitParameter("excludeUrlPatterns", "/foc/auth/login");

        return registrationBean;
    }

    public void configure(HttpSecurity httpSecurity, MvcRequestMatcher.Builder mvc) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf
                                // either fully disable:
                                .disable()
                        // OR ignore only specific endpoints:
                        // .ignoringRequestMatchers(new AntPathRequestMatcher("/foc/auth/**"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                mvc.pattern("/v2/api-docs"),
                                mvc.pattern("/v3/api-docs/**"),
                                mvc.pattern("/api-docs/**"),
                                mvc.pattern("/api-docs.yaml"),
                                mvc.pattern("/configuration/ui"),
                                mvc.pattern("/swagger-resources/**"),
                                mvc.pattern("/configuration/security"),
                                mvc.pattern("/foc/auth/login"),
                                mvc.pattern("/meta/monitor/objects"),
                                mvc.pattern("/meta/entities"),
                                mvc.pattern("/foc/obj/**"),
                                mvc.pattern("/api/**")
                        ).permitAll()
                );
    }

}