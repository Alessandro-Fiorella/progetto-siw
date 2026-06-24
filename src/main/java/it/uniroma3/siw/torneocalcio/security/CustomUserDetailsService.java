package it.uniroma3.siw.torneocalcio.security;

import it.uniroma3.siw.torneocalcio.model.Utente;
import it.uniroma3.siw.torneocalcio.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementazione di UserDetailsService richiesta da Spring Security.
 * Viene invocata automaticamente durante il processo di login per
 * caricare l'utente dal database e costruire il principal di sicurezza.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Utente utente = utenteRepository.findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("Nessun utente trovato con username: " + username));

        // Spring Security richiede il prefisso "ROLE_" per i ruoli.
        // Es. RuoloUtente.ADMIN → "ROLE_ADMIN" → verificabile con hasRole('ADMIN')
        SimpleGrantedAuthority authority =
            new SimpleGrantedAuthority("ROLE_" + utente.getRuolo().name());

        return new User(
            utente.getUsername(),
            utente.getPassword(),   //codificata con BCrypt
            List.of(authority)
        );
    }
}
