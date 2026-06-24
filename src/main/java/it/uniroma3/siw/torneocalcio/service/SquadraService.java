package it.uniroma3.siw.torneocalcio.service;

import it.uniroma3.siw.torneocalcio.model.Squadra;
import it.uniroma3.siw.torneocalcio.repository.SquadraRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SquadraService {

    @Autowired SquadraRepository squadraRepository;

    // ── Lettura ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Squadra> findAll() {
        return squadraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Squadra> findById(Long id) {
        // Carica anche i giocatori con JOIN FETCH (evita N+1 nella pagina dettaglio)
        return squadraRepository.findByIdWithGiocatori(id);
    }

    @Transactional(readOnly = true)
    public List<Squadra> findByTorneo(Long torneoId) {
        return squadraRepository.findByTorneoId(torneoId);
    }

    @Transactional(readOnly = true)
    public List<Squadra> findNonIscritteATorneo(Long torneoId) {
        return squadraRepository.findSquadreNonIscritteATorneo(torneoId);
    }

    // ── Scrittura ──────────────────────────────────────────────────────────

    @Transactional
    public Squadra save(Squadra squadra) {
        return squadraRepository.save(squadra);
    }

    @Transactional
    public Squadra update(Long id, Squadra updated) {
        Squadra squadra = squadraRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Squadra non trovata (id=" + id + ")"));
        squadra.setNome(updated.getNome());
        squadra.setAnnoFondazione(updated.getAnnoFondazione());
        squadra.setCitta(updated.getCitta());
        return squadraRepository.save(squadra);
    }

    @Transactional
    public void delete(Long id) {
        squadraRepository.deleteById(id);
    }
}
