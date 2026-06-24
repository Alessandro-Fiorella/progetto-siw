package it.uniroma3.siw.torneocalcio.controller;

import it.uniroma3.siw.torneocalcio.model.Arbitro;
import it.uniroma3.siw.torneocalcio.service.ArbitroService;
import it.uniroma3.siw.torneocalcio.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final TorneoService  torneoService;
    private final ArbitroService arbitroService;

    @Autowired
    public AdminController(TorneoService torneoService, ArbitroService arbitroService) {
        this.torneoService = torneoService;
        this.arbitroService = arbitroService;
    }

    /* ── Dashboard ─────────────────────────────────────────────────────── */

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("tornei",  torneoService.findAll());
        model.addAttribute("arbitri", arbitroService.findAll());
        return "admin/dashboard";
    }

    /* ── Gestione arbitri ──────────────────────────────────────────────── */

    @GetMapping("/arbitri/nuovo")
    public String nuovoArbitroForm(Model model) {
        model.addAttribute("arbitro", new Arbitro());
        return "admin/arbitro-form";
    }

    @PostMapping("/arbitri/nuovo")
    public String creaArbitro(@ModelAttribute Arbitro arbitro,
                              RedirectAttributes ra) {
        try {
            arbitroService.save(arbitro);
            ra.addFlashAttribute("successMsg", "Arbitro aggiunto.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin";
    }

    @GetMapping("/arbitri/{id}/modifica")
    public String modificaArbitroForm(@PathVariable Long id, Model model) {
        arbitroService.findById(id)
            .ifPresent(a -> model.addAttribute("arbitro", a));
        return "admin/arbitro-form";
    }

    @PostMapping("/arbitri/{id}/modifica")
    public String aggiornaArbitro(@PathVariable Long id,
                                  @ModelAttribute Arbitro arbitro,
                                  RedirectAttributes ra) {
        try {
            arbitroService.update(id, arbitro);
            ra.addFlashAttribute("successMsg", "Arbitro aggiornato.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin";
    }
}
