package it.uniroma3.siw.torneocalcio.service;

import it.uniroma3.siw.torneocalcio.model.Utente;
import it.uniroma3.siw.torneocalcio.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UtenteService {

    @Autowired UtenteRepository utenteRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Transactional
    public Utente registra(String username, String password) {
        if (utenteRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username \"" + username + "\" già in uso");
        }
        Utente utente = new Utente();
        utente.setUsername(username);
        utente.setPassword(passwordEncoder.encode(password));
        utente.setRuolo(Utente.RuoloUtente.USER);
        return utenteRepository.save(utente);
    }

    @Transactional(readOnly = true)
    public Utente findByUsername(String username) {
        return utenteRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + username));
    }
}
