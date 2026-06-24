package it.uniroma3.siw.torneocalcio.controller;

import it.uniroma3.siw.torneocalcio.service.TorneoService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/classifica")
@RequiredArgsConstructor
public class ClassificaController {

    @Autowired TorneoService torneoService;

    /**
     * Restituisce la pagina Thymeleaf che contiene il componente React.
     * Il componente React fetcha i dati da /api/tornei/{id}/classifica.
     * Il model passa solo il torneoId e il nome del torneo al template.
     */
    @GetMapping("/torneo/{torneoId}")
    public String classifica(@PathVariable Long torneoId, Model model) {
        torneoService.findById(torneoId).ifPresent(t -> {
            model.addAttribute("torneo", t);
            model.addAttribute("torneoId", torneoId);
        });
        return "classifica/classifica";
    }
}
