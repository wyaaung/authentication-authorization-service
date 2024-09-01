package com.wyaaung.rbac.configuration.security;

import com.wyaaung.rbac.configuration.security.jwt.JwtAuthEntryPoint;
import com.wyaaung.rbac.configuration.security.jwt.JwtAuthenticationFilter;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

  private final String[] allowedOrigins;
  private final String[] white_list_url;
  private final DaoAuthenticationProvider daoAuthenticationProvider;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthEntryPoint jwtAuthEntryPoint;

  public SecurityConfiguration(@Value("${security.allowed.origins}") String[] allowedOrigins,
                               @Value("${security.white-list.urls}") String[] white_list_url,
                               DaoAuthenticationProvider daoAuthenticationProvider,
                               JwtAuthenticationFilter jwtAuthenticationFilter,
                               JwtAuthEntryPoint jwtAuthEntryPoint) {
    this.allowedOrigins = allowedOrigins;
    this.white_list_url = white_list_url;
    this.daoAuthenticationProvider = daoAuthenticationProvider;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.jwtAuthEntryPoint = jwtAuthEntryPoint;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .csrf(AbstractHttpConfigurer::disable)
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
      .authorizeHttpRequests(requests -> requests.requestMatchers(white_list_url).permitAll().anyRequest().authenticated())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authenticationProvider(daoAuthenticationProvider)
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
    configuration.applyPermitDefaultValues();
    configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
    // setAllowCredentials(true) is important, otherwise:
    // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when
    // the request's credentials mode is 'include'.
    configuration.setAllowCredentials(true);
    // setAllowedHeaders is important! Without it, OPTIONS preflight request will fail with 403 Invalid CORS request
    configuration.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CACHE_CONTROL, HttpHeaders.CONTENT_TYPE));
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
