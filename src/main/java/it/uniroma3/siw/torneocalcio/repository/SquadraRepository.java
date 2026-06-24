package it.uniroma3.siw.torneocalcio.repository;

import it.uniroma3.siw.torneocalcio.model.Squadra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SquadraRepository extends JpaRepository<Squadra, Long> {

    boolean existsByNome(String nome);

    /**
     * Carica la squadra con tutti i suoi giocatori in una sola query.
     * Usato nella pagina di dettaglio squadra (requisito 4.1).
     * JOIN FETCH evita LazyInitializationException fuori dalla sessione JPA.
     */
    @Query("SELECT s FROM Squadra s " +
           "LEFT JOIN FETCH s.giocatori " +
           "WHERE s.id = :id")
    Optional<Squadra> findByIdWithGiocatori(@Param("id") Long id);

    /**
     * Restituisce tutte le squadre iscritte a un dato torneo.
     * Usato nel form "aggiungi partita" per popolare i menu a tendina.
     */
    @Query("SELECT s FROM Squadra s " +
           "JOIN s.tornei t " +
           "WHERE t.id = :torneoId " +
           "ORDER BY s.nome ASC")
    List<Squadra> findByTorneoId(@Param("torneoId") Long torneoId);

    /**
     * Tutte le squadre NON ancora iscritte a un torneo.
     * Usato nel form "aggiungi squadra al torneo".
     */
    @Query("SELECT s FROM Squadra s " +
           "WHERE s NOT IN (" +
           "  SELECT sq FROM Squadra sq JOIN sq.tornei t WHERE t.id = :torneoId" +
           ") ORDER BY s.nome ASC")
    List<Squadra> findSquadreNonIscritteATorneo(@Param("torneoId") Long torneoId);
}
