package it.uniroma3.siw.torneocalcio.service;

import it.uniroma3.siw.torneocalcio.model.Giocatore;
import it.uniroma3.siw.torneocalcio.model.Squadra;
import it.uniroma3.siw.torneocalcio.repository.GiocatoreRepository;
import it.uniroma3.siw.torneocalcio.repository.SquadraRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GiocatoreService {

    @Autowired GiocatoreRepository giocatoreRepository;
    @Autowired SquadraRepository squadraRepository;

    // ── Lettura ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<Giocatore> findById(Long id) {
        // JOIN FETCH sulla squadra: necessario per accedere a squadra.getId()
        // nel controller (fuori dalla sessione JPA) senza LazyInitializationException
        return giocatoreRepository.findByIdWithSquadra(id);
    }

    // ── Scrittura ──────────────────────────────────────────────────────────

    @Transactional
    public Giocatore save(Giocatore giocatore, Long squadraId) {
        Squadra squadra = squadraRepository.findById(squadraId)
            .orElseThrow(() -> new IllegalArgumentException("Squadra non trovata (id=" + squadraId + ")"));
        giocatore.setSquadra(squadra);
        return giocatoreRepository.save(giocatore);
    }

    @Transactional
    public Giocatore update(Long id, Giocatore updated) {
        // Carichiamo g con JOIN FETCH sulla squadra per poter restituire
        // squadra.getId() al controller senza aprire una nuova sessione
        Giocatore g = giocatoreRepository.findByIdWithSquadra(id)
            .orElseThrow(() -> new IllegalArgumentException("Giocatore non trovato (id=" + id + ")"));
        g.setNome(updated.getNome());
        g.setCognome(updated.getCognome());
        g.setDataNascita(updated.getDataNascita());
        g.setRuolo(updated.getRuolo());
        g.setAltezza(updated.getAltezza());
        return giocatoreRepository.save(g);
    }

    @Transactional
    public void delete(Long id) {
        giocatoreRepository.deleteById(id);
    }
}
