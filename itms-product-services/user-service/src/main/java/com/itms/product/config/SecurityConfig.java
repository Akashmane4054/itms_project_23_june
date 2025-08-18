package com.itms.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.itms.product.filter.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

   
//    
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authProvider)
//            throws Exception {
//        http.csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers(
//                    "/**"   
//                ).permitAll()
//                .requestMatchers("/auth/user/**").hasAuthority("ROLE_USER")
//                .requestMatchers("/auth/admin/**").hasAuthority("ROLE_ADMIN")
//                .anyRequest().authenticated()
//            )
//            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .authenticationProvider(authProvider)
//            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

    
    
    
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authProvider)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()   // everything open
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    
    
    
    
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
