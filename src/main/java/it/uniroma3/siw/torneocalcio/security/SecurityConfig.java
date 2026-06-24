package it.uniroma3.siw.torneocalcio.security;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configurazione centrale della sicurezza dell'applicazione.
 *
 * Ruoli (requisito 5):
 *   USER  -> utenti registrati: possono commentare le partite
 *   ADMIN -> gestione completa di tornei, squadre, partite, arbitri
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Risorse statiche
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Pagine pubbliche (requisito 4.1)
                .requestMatchers("/", "/tornei/**", "/squadre/**",
                                 "/partite/torneo/**", "/classifica/**").permitAll()
                // API REST per React
                .requestMatchers("/api/**").permitAll()
                // Auth
                .requestMatchers("/auth/login", "/auth/registrazione").permitAll()
                // Solo ADMIN (requisito 4.3)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Tutto il resto richiede autenticazione (commenti ecc.)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .userDetailsService(userDetailsService)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
