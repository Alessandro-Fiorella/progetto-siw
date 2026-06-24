package it.uniroma3.siw.torneocalcio.service;

import it.uniroma3.siw.torneocalcio.model.*;
import it.uniroma3.siw.torneocalcio.repository.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PartitaService {

    @Autowired PartitaRepository partitaRepository;
    @Autowired TorneoRepository  torneoRepository;
    @Autowired SquadraRepository squadraRepository;
    @Autowired ArbitroRepository arbitroRepository;

    // ── Lettura ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Partita> findByTorneo(Long torneoId) {
        // JOIN FETCH su squadre e arbitro → una sola query SQL (vedi PartitaRepository)
        return partitaRepository.findByTorneoIdWithTeams(torneoId);
    }

    @Transactional(readOnly = true)
    public Optional<Partita> findById(Long id) {
        // JOIN FETCH su squadre, arbitro, commenti e utenti → una sola query SQL
        return partitaRepository.findByIdWithDetails(id);
    }

    // ── Scrittura ──────────────────────────────────────────────────────────

    /**
     * Registra una nuova partita nel torneo.
     * Verifica che le due squadre siano diverse.
     */
    @Transactional
    public Partita registra(Partita partita, Long torneoId,
                            Long casaId, Long ospiteId, Long arbitroId) {
        if (casaId.equals(ospiteId)) {
            throw new IllegalArgumentException(
                "La squadra casa e la squadra ospite non possono essere la stessa");
        }

        Torneo  torneo  = torneoRepository.findById(torneoId)
            .orElseThrow(() -> new IllegalArgumentException("Torneo non trovato"));
        Squadra casa    = squadraRepository.findById(casaId)
            .orElseThrow(() -> new IllegalArgumentException("Squadra casa non trovata"));
        Squadra ospite  = squadraRepository.findById(ospiteId)
            .orElseThrow(() -> new IllegalArgumentException("Squadra ospite non trovata"));
        Arbitro arbitro = arbitroRepository.findById(arbitroId)
            .orElseThrow(() -> new IllegalArgumentException("Arbitro non trovato"));

        partita.setTorneo(torneo);
        partita.setSquadraCasa(casa);
        partita.setSquadraOspite(ospite);
        partita.setArbitro(arbitro);
        partita.setStato(StatoPartita.SCHEDULED);
        partita.setGoalsHome(0);
        partita.setGoalsAway(0);
        return partitaRepository.save(partita);
    }

    /**
     * Inserisce il risultato di una partita e la segna come PLAYED.
     */
    @Transactional
    public Partita inserisciRisultato(Long id, int goalsHome, int goalsAway) {
        if (goalsHome < 0 || goalsAway < 0) {
            throw new IllegalArgumentException("Il numero di gol non può essere negativo");
        }
        Partita p = partitaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Partita non trovata"));
        p.setGoalsHome(goalsHome);
        p.setGoalsAway(goalsAway);
        p.setStato(StatoPartita.PLAYED);
        return partitaRepository.save(p);
    }

    /**
     * Elimina una partita (solo ADMIN, requisito 4.3).
     */
    @Transactional
    public void delete(Long id) {
        partitaRepository.deleteById(id);
    }

    // ── Classifica ─────────────────────────────────────────────────────────

    /**
     * Calcola la classifica del torneo considerando solo le partite PLAYED.
     * Ordinamento: punti DESC → differenza reti DESC → gol fatti DESC.
     *
     * Usato dal REST controller /api/tornei/{id}/classifica
     * che fornisce i dati al componente React (passaggio 10).
     */
    @Transactional(readOnly = true)
    public List<ClassificaEntry> calcolaClassifica(Long torneoId) {

        // Inizializza tutte le squadre del torneo a 0 punti
        Map<Long, ClassificaEntry> mappa = new LinkedHashMap<>();
        for (Squadra s : squadraRepository.findByTorneoId(torneoId)) {
            mappa.put(s.getId(), new ClassificaEntry(s.getId(), s.getNome()));
        }

        // Aggiorna i contatori per ogni partita giocata
        List<Partita> giocate = partitaRepository
            .findByTorneoIdAndStato(torneoId, StatoPartita.PLAYED);

        for (Partita p : giocate) {
            ClassificaEntry casa = mappa.computeIfAbsent(
                p.getSquadraCasa().getId(),
                k -> new ClassificaEntry(k, p.getSquadraCasa().getNome()));
            ClassificaEntry ospite = mappa.computeIfAbsent(
                p.getSquadraOspite().getId(),
                k -> new ClassificaEntry(k, p.getSquadraOspite().getNome()));

            casa.aggiungiPartita(p.getGoalsHome(), p.getGoalsAway());
            ospite.aggiungiPartita(p.getGoalsAway(), p.getGoalsHome());
        }

        List<ClassificaEntry> classifica = new ArrayList<>(mappa.values());
        classifica.sort(
            Comparator.comparingInt(ClassificaEntry::getPunti)
            .thenComparingInt(ClassificaEntry::getDifferenzaReti)
            .thenComparingInt(ClassificaEntry::getGolFatti)
            .reversed() // Inverte l'intera catena in un colpo solo
        );
        return classifica;
    }

    // ── DTO classifica ─────────────────────────────────────────────────────

    /**
     * DTO (Data Transfer Object) che rappresenta una riga della classifica.
     * Serializzato come JSON dal REST controller e consumato da React.
     */
    @Getter
    public static class ClassificaEntry {

        private final Long   squadraId;
        private final String nomeSquadra;

        private int punti          = 0;
        private int partiteGiocate = 0;
        private int vittorie       = 0;
        private int pareggi        = 0;
        private int sconfitte      = 0;
        private int golFatti       = 0;
        private int golSubiti      = 0;

        public ClassificaEntry(Long id, String nome) {
            this.squadraId   = id;
            this.nomeSquadra = nome;
        }

        /** Aggiorna i contatori in base al risultato (dal punto di vista di questa squadra). */
        public void aggiungiPartita(int gf, int gs) {
            partiteGiocate++;
            golFatti  += gf;
            golSubiti += gs;
            if      (gf > gs) { vittorie++;  punti += 3; }
            else if (gf == gs){ pareggi++;   punti += 1; }
            else               { sconfitte++;             }
        }

        public int getDifferenzaReti() {
            return golFatti - golSubiti;
        }
    }
}
