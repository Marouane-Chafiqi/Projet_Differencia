package projet.differencia.ma.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/acces-refuse")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String accesRefuse(Model model) {
        model.addAttribute("message", "Vous n'avez pas les droits nécessaires pour accéder à cette page.");
        return "error";
    }
}
