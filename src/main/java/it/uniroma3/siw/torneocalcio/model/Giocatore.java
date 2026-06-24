package it.uniroma3.siw.torneocalcio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "giocatore")
@Getter @Setter @NoArgsConstructor
public class Giocatore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Column(nullable = false)
    private String cognome;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "data_nascita")
    private LocalDate dataNascita;

    /** es. Portiere, Difensore, Centrocampista, Attaccante */
    private String ruolo;

    @Min(value = 100, message = "Altezza minima: 100 cm")
    @Max(value = 250, message = "Altezza massima: 250 cm")
    private Integer altezza;

    /**
     * Relazione ManyToOne con Squadra.
     * Ogni giocatore appartiene a una sola squadra (LAZY: caricata su richiesta).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squadra_id")
    private Squadra squadra;
}
