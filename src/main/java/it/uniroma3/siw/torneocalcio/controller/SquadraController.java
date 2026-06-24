package it.uniroma3.siw.torneocalcio.controller;

import it.uniroma3.siw.torneocalcio.model.Squadra;
import it.uniroma3.siw.torneocalcio.service.SquadraService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/squadre")
@RequiredArgsConstructor
public class SquadraController {

    @Autowired SquadraService squadraService;

    /* ── Pubblico ──────────────────────────────────────────────────────── */

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("squadre", squadraService.findAll());
        return "squadre/lista";
    }

    @GetMapping("/{id}")
    public String dettaglio(@PathVariable Long id, Model model) {
        squadraService.findById(id)
            .ifPresentOrElse(
                s -> model.addAttribute("squadra", s),
                () -> { throw new IllegalArgumentException("Squadra non trovata"); }
            );
        return "squadre/dettaglio";
    }

    /* ── Admin: creazione ──────────────────────────────────────────────── */

    @GetMapping("/nuova")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuovaForm(Model model) {
        model.addAttribute("squadra", new Squadra());
        return "squadre/form";
    }

    @PostMapping("/nuova")
    @PreAuthorize("hasRole('ADMIN')")
    public String crea(@ModelAttribute Squadra squadra, RedirectAttributes ra) {
        Squadra saved = squadraService.save(squadra);
        ra.addFlashAttribute("successMsg", "Squadra creata con successo!");
        return "redirect:/squadre/" + saved.getId();
    }

    /* ── Admin: modifica ───────────────────────────────────────────────── */

    @GetMapping("/{id}/modifica")
    @PreAuthorize("hasRole('ADMIN')")
    public String modificaForm(@PathVariable Long id, Model model) {
        squadraService.findById(id).ifPresent(s -> model.addAttribute("squadra", s));
        return "squadre/form";
    }

    @PostMapping("/{id}/modifica")
    @PreAuthorize("hasRole('ADMIN')")
    public String aggiorna(@PathVariable Long id,
                           @ModelAttribute Squadra squadra,
                           RedirectAttributes ra) {
        squadraService.update(id, squadra);
        ra.addFlashAttribute("successMsg", "Squadra aggiornata.");
        return "redirect:/squadre/" + id;
    }

    /* ── Admin: eliminazione ───────────────────────────────────────────── */

    @PostMapping("/{id}/elimina")
    @PreAuthorize("hasRole('ADMIN')")
    public String elimina(@PathVariable Long id, RedirectAttributes ra) {
        squadraService.delete(id);
        ra.addFlashAttribute("successMsg", "Squadra eliminata.");
        return "redirect:/squadre";
    }
}
