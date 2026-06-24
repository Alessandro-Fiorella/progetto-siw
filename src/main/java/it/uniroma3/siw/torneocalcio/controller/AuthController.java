package it.uniroma3.siw.torneocalcio.controller;

import it.uniroma3.siw.torneocalcio.service.UtenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UtenteService utenteService;

    public AuthController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) model.addAttribute("errorMsg", "Username o password non validi.");
        return "auth/login";
    }

    @GetMapping("/registrazione")
    public String registrazionePage() {
        return "auth/registrazione";
    }

    @PostMapping("/registrazione")
    public String registra(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           RedirectAttributes ra) {
        if (username.isBlank()) {
            ra.addFlashAttribute("errorMsg", "Lo username non puo essere vuoto.");
            return "redirect:/auth/registrazione";
        }
        if (password.length() < 6) {
            ra.addFlashAttribute("errorMsg", "La password deve avere almeno 6 caratteri.");
            return "redirect:/auth/registrazione";
        }
        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("errorMsg", "Le password non corrispondono.");
            return "redirect:/auth/registrazione";
        }
        try {
            utenteService.registra(username, password);
            ra.addFlashAttribute("successMsg", "Registrazione completata! Effettua il login.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/auth/registrazione";
        }
    }
}
