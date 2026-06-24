package it.uniroma3.siw.torneocalcio.controller;

import it.uniroma3.siw.torneocalcio.service.TorneoService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @Autowired TorneoService torneoService;

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(required = false) String logout) {
        model.addAttribute("tornei", torneoService.findAll());
        if (logout != null) {
            model.addAttribute("logoutMsg", "Logout effettuato con successo.");
        }
        return "index";
    }
}
