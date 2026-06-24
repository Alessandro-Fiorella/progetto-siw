package it.uniroma3.siw.torneocalcio.controller;

import it.uniroma3.siw.torneocalcio.model.Giocatore;
import it.uniroma3.siw.torneocalcio.service.GiocatoreService;
import it.uniroma3.siw.torneocalcio.service.SquadraService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/giocatori")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class GiocatoreController {

    @Autowired GiocatoreService giocatoreService;
    @Autowired SquadraService   squadraService;

    /* ── Nuovo giocatore ───────────────────────────────────────────────── */

    @GetMapping("/nuovo")
    public String nuovoForm(@RequestParam Long squadraId, Model model) {
        model.addAttribute("giocatore", new Giocatore());
        squadraService.findById(squadraId)
            .ifPresent(s -> model.addAttribute("squadra", s));
        return "giocatori/form";
    }

    @PostMapping("/nuovo")
    public String crea(@ModelAttribute Giocatore giocatore,
                       @RequestParam Long squadraId,
                       RedirectAttributes ra) {
        giocatoreService.save(giocatore, squadraId);
        ra.addFlashAttribute("successMsg", "Giocatore aggiunto.");
        return "redirect:/squadre/" + squadraId;
    }

    /* ── Modifica giocatore ────────────────────────────────────────────── */

    @GetMapping("/{id}/modifica")
    public String modificaForm(@PathVariable Long id, Model model) {
        giocatoreService.findById(id).ifPresent(g -> {
            model.addAttribute("giocatore", g);
            model.addAttribute("squadra", g.getSquadra());
        });
        return "giocatori/form";
    }

    @PostMapping("/{id}/modifica")
    public String aggiorna(@PathVariable Long id,
                           @ModelAttribute Giocatore giocatore,
                           @RequestParam Long squadraId,
                           RedirectAttributes ra) {
        giocatoreService.update(id, giocatore);
        ra.addFlashAttribute("successMsg", "Giocatore aggiornato.");
        return "redirect:/squadre/" + squadraId;
    }

    /* ── Elimina giocatore ─────────────────────────────────────────────── */

    @PostMapping("/{id}/elimina")
    public String elimina(@PathVariable Long id,
                          @RequestParam Long squadraId,
                          RedirectAttributes ra) {
        giocatoreService.delete(id);
        ra.addFlashAttribute("successMsg", "Giocatore eliminato.");
        return "redirect:/squadre/" + squadraId;
    }
}
