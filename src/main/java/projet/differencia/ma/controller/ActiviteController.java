package projet.differencia.ma.controller;

import projet.differencia.ma.entities.Activite;
import projet.differencia.ma.entities.Niveau;
import projet.differencia.ma.service.ActiviteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/activites")
public class ActiviteController {

    private final ActiviteService service;

    public ActiviteController(ActiviteService service) {
        this.service = service;
    }

    @GetMapping
    public String catalogue(@RequestParam(required = false) Niveau niveau,
                            @RequestParam(required = false) String q, Model model) {
        model.addAttribute("activites", service.rechercher(q, niveau));
        model.addAttribute("niveaux", Niveau.values());
        model.addAttribute("niveauChoisi", niveau);
        model.addAttribute("q", q);
        return "activites/liste";
    }

    @GetMapping("/nouvelle")
    public String nouvelle(Model model) {
        model.addAttribute("activite", new Activite());
        model.addAttribute("niveaux", Niveau.values());
        return "activites/form";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("activite", service.findById(id));
        model.addAttribute("niveaux", Niveau.values());
        return "activites/form";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@Valid @ModelAttribute("activite") Activite activite,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("niveaux", Niveau.values());
            return "activites/form";
        }
        service.save(activite);
        ra.addFlashAttribute("message", "Activité enregistrée avec succès.");
        return "redirect:/activites";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "Activité supprimée.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/activites";
    }
}
