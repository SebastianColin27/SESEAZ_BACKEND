package com.scspd.backend.config;

import com.scspd.backend.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer; // Importa Customizer
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // Importa CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource; // Importa CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // Importa UrlBasedCorsConfigurationSource

import lombok.RequiredArgsConstructor;
import java.util.Arrays; // Importa Arrays

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http
                .cors(Customizer.withDefaults()) // *** AÑADE ESTA LÍNEA PARA HABILITAR CORS EN SECURITY ***
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authRequest ->
                        authRequest
                                .requestMatchers("/auth/**").permitAll()
                           //     .requestMatchers("/api/pdf/**").permitAll()
                                .requestMatchers("/api/**").authenticated() // Requiere autenticación para API
                                .anyRequest().denyAll() // Deniega el resto
                )
                .sessionManagement(sessionManager->
                        sessionManager
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // *** AÑADE ESTE BEAN PARA CONFIGURAR CORS ***
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Orígenes permitidos (tu frontend Angular)
        //configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedOrigins(Arrays.asList("https://seseaz-frontend.vercel.app"));
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Cabeceras permitidas (importante incluir Authorization)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Access-Control-Allow-Origi"));
        // Permitir credenciales (si usas cookies/sesiones, aunque con JWT stateless es menos común necesitarla true)
        configuration.setAllowCredentials(true); // Mantenla si angular-jwt la necesita o si tienes algún caso de uso

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración a todas las rutas bajo /
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
