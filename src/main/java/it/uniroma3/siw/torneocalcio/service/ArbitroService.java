package it.uniroma3.siw.torneocalcio.service;

import it.uniroma3.siw.torneocalcio.model.Arbitro;
import it.uniroma3.siw.torneocalcio.repository.ArbitroRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArbitroService {

    @Autowired ArbitroRepository arbitroRepository;

    // ── Lettura ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Arbitro> findAll() {
        return arbitroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Arbitro> findById(Long id) {
        return arbitroRepository.findById(id);
    }

    // ── Scrittura ──────────────────────────────────────────────────────────

    @Transactional
    public Arbitro save(Arbitro arbitro) {
        if (arbitroRepository.existsByCodiceArbitrale(arbitro.getCodiceArbitrale())) {
            throw new IllegalArgumentException(
                "Codice arbitrale \"" + arbitro.getCodiceArbitrale() + "\" già presente");
        }
        return arbitroRepository.save(arbitro);
    }

    @Transactional
    public Arbitro update(Long id, Arbitro updated) {
        Arbitro a = arbitroRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Arbitro non trovato (id=" + id + ")"));

        // Verifica unicità del codice solo se è cambiato
        if (!a.getCodiceArbitrale().equals(updated.getCodiceArbitrale()) &&
            arbitroRepository.existsByCodiceArbitraleAndIdNot(updated.getCodiceArbitrale(), id)) {
            throw new IllegalArgumentException(
                "Codice arbitrale \"" + updated.getCodiceArbitrale() + "\" già in uso");
        }

        a.setNome(updated.getNome());
        a.setCognome(updated.getCognome());
        a.setCodiceArbitrale(updated.getCodiceArbitrale());
        return arbitroRepository.save(a);
    }
}
