package it.uniroma3.siw.torneocalcio.repository;

import it.uniroma3.siw.torneocalcio.model.Giocatore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiocatoreRepository extends JpaRepository<Giocatore, Long> {

    /**
     * Carica il giocatore con la squadra già inizializzata.
     * Necessario per accedere a giocatore.getSquadra().getId()
     * fuori dalla sessione JPA (es. redirect nel controller).
     */
    @Query("SELECT g FROM Giocatore g " +
           "JOIN FETCH g.squadra " +
           "WHERE g.id = :id")
    Optional<Giocatore> findByIdWithSquadra(@Param("id") Long id);
}
