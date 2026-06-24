package it.uniroma3.siw.torneocalcio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "torneo")
@Getter @Setter @NoArgsConstructor
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "L'anno è obbligatorio")
    @Min(value = 1900, message = "Anno non valido")
    @Column(nullable = false)
    private Integer anno;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    /**
     * Relazione ManyToMany con Squadra.
     * LAZY: le squadre vengono caricate solo quando servono.
     * La join table si chiama "torneo_squadra".
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "torneo_squadra",
        joinColumns      = @JoinColumn(name = "torneo_id"),
        inverseJoinColumns = @JoinColumn(name = "squadra_id")
    )
    private Set<Squadra> squadre = new HashSet<>();

    /**
     * Relazione OneToMany con Partita.
     * CascadeType.ALL: eliminando un torneo si eliminano anche le partite.
     */
    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Partita> partite = new ArrayList<>();
}
