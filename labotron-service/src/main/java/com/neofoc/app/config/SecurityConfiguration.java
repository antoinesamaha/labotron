package com.neofoc.app.config;

import com.neofoc.springboot.config.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
public class SecurityConfiguration {

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList(allowedOrigin));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Collections.singletonList("*"));
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
                        .requestMatchers(
                                mvc.pattern("/v2/api-docs"),
                                mvc.pattern("/v3/api-docs/**"),
                                mvc.pattern("/api-docs/**"),
                                mvc.pattern("/api-docs.yaml"),
                                mvc.pattern("/configuration/ui"),
                                mvc.pattern("/swagger-resources/**"),
                                mvc.pattern("/configuration/security"),
                                mvc.pattern("/foc/auth/login"),
                                mvc.pattern("/meta/entities"),
                                //ALERT: Activate that filter to enable JWT authentication
                                mvc.pattern("/foc/obj/**"),
                                mvc.pattern("/api/**")
//                                mvc.pattern("/swagger-ui.html"),
//                                mvc.pattern("/swagger-ui/**"),
//                                mvc.pattern("/webjars/**"),
//                                mvc.pattern("/api/event/all"),
//                                mvc.pattern("/api/event/filter"),
//                                mvc.pattern("/api/eventComment/all"),
//                                mvc.pattern("/api/eventComment/all/detailed"),
//                                mvc.pattern("/api/eventComment/event/**"),
//                                mvc.pattern("/api/eventComment/all/with-like/**"),
//                                mvc.pattern("/api/userProfile/register"),
//                                mvc.pattern("/api/event/all/with-like/**"),
//                                mvc.pattern("/api/eventAccessibility"),
//                                mvc.pattern("/api/eventCategory"),
//                                mvc.pattern("/api/location"),
//                                mvc.pattern("/api/publishStatus")
                        ).permitAll()
//                        .requestMatchers(
//                                mvc.pattern("/public/**"),
//                                mvc.pattern("/v1/otp/verify"),
//                                mvc.pattern("/v1/otp/generate"),
//                                mvc.pattern("/application-settings"),
//                                mvc.pattern(Constants.NOTIFICATION_ENDPOINT)
//                        ).permitAll()
//                        .requestMatchers(
//                                mvc.pattern("/XXX/foc/obj/**")
//                        ).authenticated()
                );
    }

}