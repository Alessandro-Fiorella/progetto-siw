package it.uniroma3.siw.torneocalcio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "squadra")
@Getter @Setter @NoArgsConstructor
public class Squadra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome della squadra è obbligatorio")
    @Column(nullable = false)
    private String nome;

    @Column(name = "anno_fondazione")
    private Integer annoFondazione;

    private String citta;

    /**
     * Relazione OneToMany con Giocatore.
     * Un giocatore viene eliminato se la squadra viene eliminata (cascade).
     */
    @OneToMany(mappedBy = "squadra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Giocatore> giocatori = new ArrayList<>();

    /**
     * Lato inverso della relazione ManyToMany con Torneo.
     * mappedBy indica che Torneo è il proprietario della join table.
     */
    @ManyToMany(mappedBy = "squadre", fetch = FetchType.LAZY)
    private Set<Torneo> tornei = new HashSet<>();


}
