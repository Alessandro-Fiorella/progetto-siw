package it.uniroma3.siw.torneocalcio.repository;

import it.uniroma3.siw.torneocalcio.model.Partita;
import it.uniroma3.siw.torneocalcio.model.StatoPartita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartitaRepository extends JpaRepository<Partita, Long> {

    /**
     * Calendario completo di un torneo: squadre e arbitro caricati
     * in una sola query (JOIN FETCH, evita N+1).
     */
    @Query("SELECT p FROM Partita p " +
           "JOIN FETCH p.squadraCasa " +
           "JOIN FETCH p.squadraOspite " +
           "LEFT JOIN FETCH p.arbitro " +
           "WHERE p.torneo.id = :torneoId " +
           "ORDER BY p.dataOra ASC NULLS LAST")
    List<Partita> findByTorneoIdWithTeams(@Param("torneoId") Long torneoId);

    /**
     * Solo le partite già giocate di un torneo (per il calcolo classifica).
     */
    @Query("SELECT p FROM Partita p " +
           "JOIN FETCH p.squadraCasa " +
           "JOIN FETCH p.squadraOspite " +
           "WHERE p.torneo.id = :torneoId " +
           "AND p.stato = :stato")
    List<Partita> findByTorneoIdAndStato(
            @Param("torneoId") Long torneoId,
            @Param("stato") StatoPartita stato);

    /**
     * Dettaglio partita con commenti e utenti.
     * DISTINCT perché il JOIN su commenti può produrre righe duplicate.
     */
    @Query("SELECT DISTINCT p FROM Partita p " +
           "JOIN FETCH p.squadraCasa " +
           "JOIN FETCH p.squadraOspite " +
           "LEFT JOIN FETCH p.arbitro " +
           "LEFT JOIN FETCH p.commenti c " +
           "LEFT JOIN FETCH c.utente " +
           "WHERE p.id = :id")
    Optional<Partita> findByIdWithDetails(@Param("id") Long id);
}
