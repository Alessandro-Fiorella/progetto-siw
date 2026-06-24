package it.uniroma3.siw.torneocalcio.controller;

import it.uniroma3.siw.torneocalcio.model.Partita;
import it.uniroma3.siw.torneocalcio.service.*;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/partite")
@RequiredArgsConstructor
public class PartitaController {

    @Autowired PartitaService partitaService;
    @Autowired TorneoService  torneoService;
    @Autowired SquadraService squadraService;
    @Autowired ArbitroService arbitroService;
    @Autowired CommentoService commentoService;

    /* ── Pubblico: calendario ──────────────────────────────────────────── */

    @GetMapping("/torneo/{torneoId}")
    public String calendario(@PathVariable Long torneoId, Model model) {
        torneoService.findById(torneoId)
            .ifPresent(t -> model.addAttribute("torneo", t));
        model.addAttribute("partite", partitaService.findByTorneo(torneoId));
        return "partite/calendario";
    }

    /* ── Dettaglio partita (autenticati per i commenti) ────────────────── */

    @GetMapping("/{id}")
    public String dettaglio(@PathVariable Long id, Model model,
                            Authentication auth) {
        partitaService.findById(id)
            .ifPresentOrElse(
                p -> model.addAttribute("partita", p),
                () -> { throw new IllegalArgumentException("Partita non trovata"); }
            );
        if (auth != null) {
            model.addAttribute("utenteCorrente", auth.getName());
        }
        return "partite/dettaglio";
    }

    /* ── Admin: nuova partita ──────────────────────────────────────────── */

    @GetMapping("/nuova")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuovaForm(@RequestParam Long torneoId, Model model) {
        torneoService.findById(torneoId)
            .ifPresent(t -> model.addAttribute("torneo", t));
        model.addAttribute("squadre", squadraService.findByTorneo(torneoId));
        model.addAttribute("arbitri", arbitroService.findAll());
        model.addAttribute("partita", new Partita());
        return "partite/form";
    }

    @PostMapping("/nuova")
    @PreAuthorize("hasRole('ADMIN')")
    public String crea(@ModelAttribute Partita partita,
                       @RequestParam Long torneoId,
                       @RequestParam Long squadraCasaId,
                       @RequestParam Long squadraOspiteId,
                       @RequestParam Long arbitroId,
                       RedirectAttributes ra) {
        try {
            partitaService.registra(partita, torneoId,
                    squadraCasaId, squadraOspiteId, arbitroId);
            ra.addFlashAttribute("successMsg", "Partita registrata.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/partite/torneo/" + torneoId;
    }

    /* ── Admin: inserimento risultato ──────────────────────────────────── */

    @PostMapping("/{id}/risultato")
    @PreAuthorize("hasRole('ADMIN')")
    public String inserisciRisultato(@PathVariable Long id,
                                     @RequestParam int goalsHome,
                                     @RequestParam int goalsAway,
                                     RedirectAttributes ra) {
        try {
            partitaService.inserisciRisultato(id, goalsHome, goalsAway);
            ra.addFlashAttribute("successMsg", "Risultato inserito.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/partite/" + id;
    }

    /* ── Admin: eliminazione ───────────────────────────────────────────── */

    @PostMapping("/{id}/elimina")
    @PreAuthorize("hasRole('ADMIN')")
    public String elimina(@PathVariable Long id,
                          @RequestParam Long torneoId,
                          RedirectAttributes ra) {
        partitaService.delete(id);
        ra.addFlashAttribute("successMsg", "Partita eliminata.");
        return "redirect:/partite/torneo/" + torneoId;
    }

    /* ── Utente registrato: commenti ───────────────────────────────────── */

    @PostMapping("/{id}/commenti/aggiungi")
    @PreAuthorize("isAuthenticated()")
    public String aggiungiCommento(@PathVariable Long id,
                                   @RequestParam String testo,
                                   Authentication auth,
                                   RedirectAttributes ra) {
        try {
            commentoService.aggiungi(id, testo, auth.getName());
            ra.addFlashAttribute("successMsg", "Commento aggiunto.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/partite/" + id;
    }

    @PostMapping("/commenti/{commentoId}/modifica")
    @PreAuthorize("isAuthenticated()")
    public String modificaCommento(@PathVariable Long commentoId,
                                   @RequestParam String testo,
                                   @RequestParam Long partitaId,
                                   Authentication auth,
                                   RedirectAttributes ra) {
        try {
            commentoService.modifica(commentoId, testo, auth.getName());
            ra.addFlashAttribute("successMsg", "Commento modificato.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/partite/" + partitaId;
    }

    @PostMapping("/commenti/{commentoId}/elimina")
    @PreAuthorize("isAuthenticated()")
    public String eliminaCommento(@PathVariable Long commentoId,
                                  Authentication auth,
                                  RedirectAttributes ra) {
        try {
            Long partitaId = commentoService.elimina(commentoId, auth.getName());
            ra.addFlashAttribute("successMsg", "Commento eliminato.");
            return "redirect:/partite/" + partitaId;
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/";
        }
    }
}
