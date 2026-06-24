package it.uniroma3.siw.torneocalcio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "arbitro")
@Getter @Setter @NoArgsConstructor
public class Arbitro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Column(nullable = false)
    private String cognome;

    @NotBlank(message = "Il codice arbitrale è obbligatorio")
    @Column(name = "codice_arbitrale", unique = true, nullable = false)
    private String codiceArbitrale;

    /**
     * Un arbitro può dirigere più partite.
     * LAZY: le partite vengono caricate solo se richiesto.
     */
    @OneToMany(mappedBy = "arbitro", fetch = FetchType.LAZY)
    private List<Partita> partite = new ArrayList<>();
}
