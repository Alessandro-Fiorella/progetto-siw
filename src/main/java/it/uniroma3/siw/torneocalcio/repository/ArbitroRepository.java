package it.uniroma3.siw.torneocalcio.repository;

import it.uniroma3.siw.torneocalcio.model.Arbitro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArbitroRepository extends JpaRepository<Arbitro, Long> {

    /** Verifica unicità del codice arbitrale prima del salvataggio. */
    boolean existsByCodiceArbitrale(String codiceArbitrale);

    /** Usato per aggiornamento: verifica unicità escludendo l'arbitro corrente. */
    boolean existsByCodiceArbitraleAndIdNot(String codiceArbitrale, Long id);
}
