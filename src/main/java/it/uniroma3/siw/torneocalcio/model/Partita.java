package it.uniroma3.siw.torneocalcio.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "partita")
@Getter @Setter @NoArgsConstructor
public class Partita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "data_ora")
    private LocalDateTime dataOra;

    private String luogo;

    @Column(name = "goals_home")
    private Integer goalsHome = 0;

    @Column(name = "goals_away")
    private Integer goalsAway = 0;

    @Enumerated(EnumType.STRING)
    private StatoPartita stato = StatoPartita.SCHEDULED;

    /**
     * La partita appartiene a un torneo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;

    /**
     * La squadra che gioca in casa.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squadra_casa_id", nullable = false)
    private Squadra squadraCasa;

    /**
     * La squadra ospite.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squadra_ospite_id", nullable = false)
    private Squadra squadraOspite;

    /**
     * L'arbitro che dirige la partita.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitro_id")
    private Arbitro arbitro;

    /**
     * Commenti degli utenti sulla partita.
     * CascadeType.ALL: eliminando la partita si eliminano anche i commenti.
     */
    @OneToMany(mappedBy = "partita", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Commento> commenti = new ArrayList<>();
}
