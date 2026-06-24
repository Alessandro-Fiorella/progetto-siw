package it.uniroma3.siw.torneocalcio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "utente")
@Getter @Setter @NoArgsConstructor
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Lo username è obbligatorio")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuoloUtente ruolo = RuoloUtente.USER;

    public enum RuoloUtente {
        USER,   // utente registrato: può commentare le partite
        ADMIN   // amministratore: gestione completa del sistema
    }

    
}
