package com.engineering_lab.hunger.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import com.engineering_lab.hunger.common.exception.ApiAccessDeniedHandler;
import com.engineering_lab.hunger.common.exception.ApiAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        @Order(1)
        SecurityFilterChain authenticationSecurity(
                        HttpSecurity http,
                        ApiAuthenticationEntryPoint authenticationEntryPoint,
                        ApiAccessDeniedHandler accessDeniedHandler) throws Exception {

                CookieCsrfTokenRepository csrfRepository = new CookieCsrfTokenRepository();

                csrfRepository.setCookieName("XSRF-TOKEN");
                csrfRepository.setHeaderName("X-XSRF-TOKEN");
                csrfRepository.setCookiePath("/");

                return http
                                .securityMatcher("/api/v1/auth/**")

                                .cors(Customizer.withDefaults())

                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(csrfRepository)
                                                .csrfTokenRequestHandler(
                                                                new CsrfTokenRequestAttributeHandler()))

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS))

                                .requestCache(cache -> cache.disable())

                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable())
                                .logout(logout -> logout.disable())

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/v1/auth/csrf")
                                                .permitAll()

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/v1/auth/login",
                                                                "/api/v1/auth/refresh",
                                                                "/api/v1/auth/logout",
                                                                "/api/v1/auth/activate")
                                                .permitAll()

                                                .anyRequest().denyAll())

                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                authenticationEntryPoint)
                                                .accessDeniedHandler(
                                                                accessDeniedHandler))

                                .build();
        }

        @Bean
        @Order(2)
        SecurityFilterChain apiSecurity(
                        HttpSecurity http,
                        ApiAuthenticationEntryPoint authenticationEntryPoint,
                        ApiAccessDeniedHandler accessDeniedHandler) throws Exception {

                return http
                                .securityMatcher("/api/**")

                                .cors(Customizer.withDefaults())

                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS))

                                .requestCache(cache -> cache.disable())

                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable())
                                .logout(logout -> logout.disable())

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/v1/users/register")
                                                .permitAll()

                                                .anyRequest().authenticated())

                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                authenticationEntryPoint)
                                                .accessDeniedHandler(
                                                                accessDeniedHandler))

                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(Customizer.withDefaults())
                                                .authenticationEntryPoint(
                                                                authenticationEntryPoint))

                                .build();
        }

        @Bean
        @Order(3)
        SecurityFilterChain fallbackSecurity(
                        HttpSecurity http,
                        ApiAuthenticationEntryPoint authenticationEntryPoint,
                        ApiAccessDeniedHandler accessDeniedHandler) throws Exception {

                return http
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS))

                                .requestCache(cache -> cache.disable())

                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable())
                                .logout(logout -> logout.disable())

                                .authorizeHttpRequests(auth -> auth
                                                .anyRequest().denyAll())

                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                authenticationEntryPoint)
                                                .accessDeniedHandler(
                                                                accessDeniedHandler))

                                .build();
        }
}
