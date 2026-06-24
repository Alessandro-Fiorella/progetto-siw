package it.uniroma3.siw.torneocalcio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "commento")
@Getter @Setter @NoArgsConstructor
public class Commento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il commento non può essere vuoto")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String testo;

    @Column(name = "data_creazione")
    private LocalDateTime dataCreazione = LocalDateTime.now();

    /**
     * L'utente che ha scritto il commento.
     * Un utente può modificare solo i propri commenti.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    /**
     * La partita a cui si riferisce il commento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partita_id", nullable = false)
    private Partita partita;
}
