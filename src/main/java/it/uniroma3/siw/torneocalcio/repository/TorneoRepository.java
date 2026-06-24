package it.uniroma3.siw.torneocalcio.repository;

import it.uniroma3.siw.torneocalcio.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Long> {

    boolean existsByNomeAndAnno(String nome, Integer anno);

    /**
     * OTTIMIZZAZIONE: JOIN FETCH su squadre evita il problema N+1.
     * Senza JOIN FETCH, Hibernate eseguirebbe 1 query per i tornei +
     * N query aggiuntive (una per ogni torneo) per caricare le squadre.
     * Con JOIN FETCH tutto viene risolto in una sola query SQL.
     *
     * DISTINCT è necessario perché il JOIN può produrre righe duplicate
     * lato Java quando un torneo ha più squadre.
     */
    @Query("SELECT DISTINCT t FROM Torneo t " +
           "LEFT JOIN FETCH t.squadre " +
           "ORDER BY t.anno DESC, t.nome ASC")
    List<Torneo> findAllWithSquadre();

    /**
     * Carica un torneo con le sue squadre per la pagina di dettaglio.
     * LEFT JOIN FETCH: restituisce il torneo anche se non ha squadre.
     */
    @Query("SELECT t FROM Torneo t " +
           "LEFT JOIN FETCH t.squadre " +
           "WHERE t.id = :id")
    Optional<Torneo> findByIdWithSquadre(@Param("id") Long id);
}
