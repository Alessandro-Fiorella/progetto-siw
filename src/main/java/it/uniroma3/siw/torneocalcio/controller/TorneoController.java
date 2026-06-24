package it.uniroma3.siw.torneocalcio.controller;

import it.uniroma3.siw.torneocalcio.model.Torneo;
import it.uniroma3.siw.torneocalcio.repository.TorneoRepository;
import it.uniroma3.siw.torneocalcio.service.SquadraService;
import it.uniroma3.siw.torneocalcio.service.TorneoService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tornei")
@RequiredArgsConstructor
public class TorneoController {

    private final TorneoRepository torneoRepository;
    @Autowired TorneoService  torneoService;
    @Autowired SquadraService squadraService;

    TorneoController(TorneoRepository torneoRepository) {
        this.torneoRepository = torneoRepository;
    }

    /* ── Pubblico ──────────────────────────────────────────────────────── */

    @GetMapping
    public String lista(Model model) {


        model.addAttribute("tornei", torneoService.findAll());
        return "tornei/lista";

    }

    @GetMapping("/{id}")
    public String dettaglio(@PathVariable Long id, Model model) {
        torneoService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Torneo non trovato"));
        model.addAttribute("torneo", torneoService.findById(id).get());
        return "tornei/dettaglio";
    }

    /* ── Admin: creazione ──────────────────────────────────────────────── */

    @GetMapping("/nuovo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuovoForm(Model model) {
        model.addAttribute("torneo", new Torneo());
        return "tornei/form";
    }

    @PostMapping("/nuovo")
    @PreAuthorize("hasRole('ADMIN')")
    public String crea(@ModelAttribute Torneo torneo, RedirectAttributes ra) {
        try {
            Torneo saved = torneoService.save(torneo);
            ra.addFlashAttribute("successMsg", "Torneo creato con successo!");
            return "redirect:/tornei/" + saved.getId();
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/tornei/nuovo";
        }
    }

    /* ── Admin: modifica ───────────────────────────────────────────────── */

    @GetMapping("/{id}/modifica")
    @PreAuthorize("hasRole('ADMIN')")
    public String modificaForm(@PathVariable Long id, Model model) {
        torneoService.findById(id).ifPresent(t -> model.addAttribute("torneo", t));
        return "tornei/form";
    }

    @PostMapping("/{id}/modifica")
    @PreAuthorize("hasRole('ADMIN')")
    public String aggiorna(@PathVariable Long id,
                           @ModelAttribute Torneo torneo,
                           RedirectAttributes ra) {
        try {
            torneoService.update(id, torneo);
            ra.addFlashAttribute("successMsg", "Torneo aggiornato.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/tornei/" + id;
    }

    /* ── Admin: gestione squadre nel torneo ────────────────────────────── */

    @GetMapping("/{id}/squadre/aggiungi")
    @PreAuthorize("hasRole('ADMIN')")
    public String aggiungiSquadraForm(@PathVariable Long id, Model model) {
        torneoService.findById(id).ifPresent(t -> model.addAttribute("torneo", t));
        model.addAttribute("squadreDisponibili",
                squadraService.findNonIscritteATorneo(id));
        return "tornei/aggiungi-squadra";
    }

    @PostMapping("/{torneoId}/squadre/{squadraId}/aggiungi")
    @PreAuthorize("hasRole('ADMIN')")
    public String aggiungiSquadra(@PathVariable Long torneoId,
                                  @PathVariable Long squadraId,
                                  RedirectAttributes ra) {
        try {
            torneoService.aggiungiSquadra(torneoId, squadraId);
            ra.addFlashAttribute("successMsg", "Squadra aggiunta al torneo.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/tornei/" + torneoId;
    }

    @PostMapping("/{torneoId}/squadre/{squadraId}/rimuovi")
    @PreAuthorize("hasRole('ADMIN')")
    public String rimuoviSquadra(@PathVariable Long torneoId,
                                  @PathVariable Long squadraId,
                                  RedirectAttributes ra) {
        try {
            torneoService.rimuoviSquadra(torneoId, squadraId);
            ra.addFlashAttribute("successMsg", "Squadra rimossa dal torneo.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/tornei/" + torneoId;
    }
}
