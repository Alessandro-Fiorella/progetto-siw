package it.uniroma3.siw.torneocalcio.repository;

import it.uniroma3.siw.torneocalcio.model.Commento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentoRepository extends JpaRepository<Commento, Long> {

    /**
     * Cerca un commento per id E per username del proprietario.
     * Garantisce che un utente possa modificare/eliminare solo i propri commenti
     * (requisito 4.2: "un utente può modificare solo i propri commenti").
     */
    Optional<Commento> findByIdAndUtenteUsername(Long id, String username);
}
