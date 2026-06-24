package it.uniroma3.siw.torneocalcio.service;

import it.uniroma3.siw.torneocalcio.model.Squadra;
import it.uniroma3.siw.torneocalcio.model.Torneo;
import it.uniroma3.siw.torneocalcio.repository.SquadraRepository;
import it.uniroma3.siw.torneocalcio.repository.TorneoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TorneoService {

    @Autowired TorneoRepository torneoRepository;
    @Autowired SquadraRepository squadraRepository;

    // ── Lettura ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Torneo> findAll() {
        return torneoRepository.findAllWithSquadre();
    }

    @Transactional(readOnly = true)
    public Optional<Torneo> findById(Long id) {
        return torneoRepository.findByIdWithSquadre(id);
    }

    @Transactional(readOnly = true)
    public List<Torneo> findAllSemplice() {
        // Versione senza JOIN FETCH: usata dove le squadre non servono
        return torneoRepository.findAll();
    }

    // ── Scrittura ──────────────────────────────────────────────────────────

    @Transactional
    public Torneo save(Torneo torneo) {
        if (torneo.getId() == null &&
            torneoRepository.existsByNomeAndAnno(torneo.getNome(), torneo.getAnno())) {
            throw new IllegalArgumentException(
                "Esiste già un torneo chiamato \"" + torneo.getNome() +
                "\" per l'anno " + torneo.getAnno());
        }
        return torneoRepository.save(torneo);
    }

    @Transactional
    public Torneo update(Long id, Torneo updated) {
        Torneo torneo = torneoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Torneo non trovato (id=" + id + ")"));
        torneo.setNome(updated.getNome());
        torneo.setAnno(updated.getAnno());
        torneo.setDescrizione(updated.getDescrizione());
        return torneoRepository.save(torneo);
    }

    @Transactional
    public void aggiungiSquadra(Long torneoId, Long squadraId) {
        Torneo torneo = torneoRepository.findById(torneoId)
            .orElseThrow(() -> new IllegalArgumentException("Torneo non trovato"));
        Squadra squadra = squadraRepository.findById(squadraId)
            .orElseThrow(() -> new IllegalArgumentException("Squadra non trovata"));

        torneo.getSquadre().add(squadra);
        squadra.getTornei().add(torneo);
        // Hibernate persiste la join table automaticamente al flush
    }

    @Transactional
    public void rimuoviSquadra(Long torneoId, Long squadraId) {
        Torneo torneo = torneoRepository.findById(torneoId)
            .orElseThrow(() -> new IllegalArgumentException("Torneo non trovato"));
        Squadra squadra = squadraRepository.findById(squadraId)
            .orElseThrow(() -> new IllegalArgumentException("Squadra non trovata"));

        torneo.getSquadre().remove(squadra);
        squadra.getTornei().remove(torneo);
    }
}
