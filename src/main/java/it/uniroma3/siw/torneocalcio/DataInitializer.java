package it.uniroma3.siw.torneocalcio;

import it.uniroma3.siw.torneocalcio.model.Utente;
import it.uniroma3.siw.torneocalcio.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Eseguito automaticamente all'avvio dell'applicazione.
 *
 * Crea un utente ADMIN di default se non ne esiste già uno,
 * così il sistema è immediatamente utilizzabile senza doversi
 * registrare manualmente come admin dal DB.
 *
 * Credenziali di default:
 *   username : admin
 *   password : admin123
 *
 * ATTENZIONE: cambiare la password in produzione.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    @Autowired UtenteRepository utenteRepository;
    @Autowired PasswordEncoder  passwordEncoder;

    @Override
    public void run(String... args) {
        if (!utenteRepository.existsByUsername("admin")) {
            Utente admin = new Utente();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRuolo(Utente.RuoloUtente.ADMIN);
            utenteRepository.save(admin);

            System.out.println("===========================================");
            System.out.println("  Utente admin creato:");
            System.out.println("  username: admin");
            System.out.println("  password: admin123");
            System.out.println("===========================================");
        }
    }
}
