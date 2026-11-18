package projet.differencia.ma.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projet.differencia.ma.dto.ChangerMotDePasseForm;
import projet.differencia.ma.service.UtilisateurService;

import java.security.Principal;

@Controller
@RequestMapping("/profil")
public class ProfilController {

    private final UtilisateurService service;

    public ProfilController(UtilisateurService service) {
        this.service = service;
    }

    @GetMapping("/mot-de-passe")
    public String formulaire(Model model) {
        model.addAttribute("form", new ChangerMotDePasseForm());
        return "profil/mot-de-passe";
    }

    @PostMapping("/mot-de-passe")
    public String changer(@Valid @ModelAttribute("form") ChangerMotDePasseForm form,
                          BindingResult result, Principal principal, RedirectAttributes ra) {
        if (form.getNouveau() != null && form.getConfirmation() != null
                && !form.getNouveau().equals(form.getConfirmation())) {
            result.rejectValue("confirmation", "mismatch", "Les mots de passe ne correspondent pas");
        }
        if (result.hasErrors()) {
            return "profil/mot-de-passe";
        }
        try {
            service.changerMotDePasse(principal.getName(), form.getAncien(), form.getNouveau());
        } catch (IllegalStateException e) {
            result.rejectValue("ancien", "incorrect", e.getMessage());
            return "profil/mot-de-passe";
        }
        ra.addFlashAttribute("message", "Mot de passe modifié avec succès.");
        return "redirect:/";
    }
}
