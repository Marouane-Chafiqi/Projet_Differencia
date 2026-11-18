package projet.differencia.ma.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projet.differencia.ma.dto.UtilisateurForm;
import projet.differencia.ma.entities.Role;
import projet.differencia.ma.service.UtilisateurService;

import java.security.Principal;

@Controller
@RequestMapping("/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService service;

    public UtilisateurController(UtilisateurService service) {
        this.service = service;
    }

    @GetMapping
    public String liste(Model model, Principal principal) {
        model.addAttribute("utilisateurs", service.findAll());
        model.addAttribute("moi", principal.getName());
        return "utilisateurs/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("form", new UtilisateurForm());
        model.addAttribute("roles", Role.values());
        return "utilisateurs/form";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@Valid @ModelAttribute("form") UtilisateurForm form,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (form.getPassword() != null && form.getConfirmation() != null
                && !form.getPassword().equals(form.getConfirmation())) {
            result.rejectValue("confirmation", "mismatch", "Les mots de passe ne correspondent pas");
        }
        if (!result.hasErrors()) {
            try {
                service.creer(form.getUsername(), form.getNomComplet(), form.getPassword(), form.getRole());
            } catch (IllegalStateException e) {
                result.rejectValue("username", "exists", e.getMessage());
            }
        }
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "utilisateurs/form";
        }
        ra.addFlashAttribute("message", "Utilisateur créé avec succès.");
        return "redirect:/utilisateurs";
    }

    @PostMapping("/{id}/basculer")
    public String basculer(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        try {
            service.basculerActif(id, principal.getName());
            ra.addFlashAttribute("message", "Statut du compte mis à jour.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/utilisateurs";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        try {
            service.supprimer(id, principal.getName());
            ra.addFlashAttribute("message", "Utilisateur supprimé.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/utilisateurs";
    }
}
