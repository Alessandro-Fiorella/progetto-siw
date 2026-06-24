package it.uniroma3.siw.torneocalcio.service;

import it.uniroma3.siw.torneocalcio.model.Commento;
import it.uniroma3.siw.torneocalcio.model.Partita;
import it.uniroma3.siw.torneocalcio.model.Utente;
import it.uniroma3.siw.torneocalcio.repository.CommentoRepository;
import it.uniroma3.siw.torneocalcio.repository.PartitaRepository;
import it.uniroma3.siw.torneocalcio.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentoService {

    @Autowired CommentoRepository commentoRepository;
    @Autowired PartitaRepository  partitaRepository;
    @Autowired UtenteRepository   utenteRepository;

    @Transactional
    public Commento aggiungi(Long partitaId, String testo, String username) {
        Partita partita = partitaRepository.findById(partitaId)
            .orElseThrow(() -> new IllegalArgumentException("Partita non trovata"));
        Utente utente = utenteRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        Commento c = new Commento();
        c.setTesto(testo);
        c.setPartita(partita);
        c.setUtente(utente);
        c.setDataCreazione(LocalDateTime.now());
        return commentoRepository.save(c);
    }

    /**
     * Un utente può modificare solo i propri commenti.
     * La verifica è delegata alla query del repository:
     * findByIdAndUtenteUsername restituisce Optional.empty()
     * se il commento non appartiene all'utente → eccezione.
     */
    @Transactional
    public Commento modifica(Long commentoId, String nuovoTesto, String username) {
        Commento c = commentoRepository.findByIdAndUtenteUsername(commentoId, username)
            .orElseThrow(() -> new IllegalArgumentException(
                "Commento non trovato o non sei autorizzato a modificarlo"));
        c.setTesto(nuovoTesto);
        return commentoRepository.save(c);
    }

    /**
     * Restituisce l'id della partita per il redirect nel controller.
     */
    @Transactional
    public Long elimina(Long commentoId, String username) {
        Commento c = commentoRepository.findByIdAndUtenteUsername(commentoId, username)
            .orElseThrow(() -> new IllegalArgumentException(
                "Commento non trovato o non sei autorizzato a eliminarlo"));
        Long partitaId = c.getPartita().getId();
        commentoRepository.delete(c);
        return partitaId;
    }
}
