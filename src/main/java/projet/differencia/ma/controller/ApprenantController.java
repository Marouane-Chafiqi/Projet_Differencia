package projet.differencia.ma.controller;

import projet.differencia.ma.entities.Affectation;
import projet.differencia.ma.entities.Apprenant;
import projet.differencia.ma.entities.Niveau;
import projet.differencia.ma.entities.Statut;
import projet.differencia.ma.service.AffectationService;
import projet.differencia.ma.service.ApprenantService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/apprenants")
public class ApprenantController {

    private final ApprenantService service;
    private final AffectationService affectationService;

    public ApprenantController(ApprenantService service, AffectationService affectationService) {
        this.service = service;
        this.affectationService = affectationService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Niveau niveau, Model model) {
        model.addAttribute("apprenants", service.rechercher(q, niveau));
        model.addAttribute("niveaux", Niveau.values());
        model.addAttribute("q", q);
        model.addAttribute("niveauChoisi", niveau);
        return "apprenants/liste";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Apprenant apprenant = service.findById(id);
        List<Affectation> affectations = affectationService.parApprenant(id);
        long termines = affectations.stream().filter(a -> a.getStatut() == Statut.TERMINE).count();
        model.addAttribute("apprenant", apprenant);
        model.addAttribute("affectations", affectations);
        model.addAttribute("termines", termines);
        model.addAttribute("moyenne", affectationService.moyenne(affectations));
        return "apprenants/detail";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("apprenant", new Apprenant());
        model.addAttribute("niveaux", Niveau.values());
        return "apprenants/form";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("apprenant", service.findById(id));
        model.addAttribute("niveaux", Niveau.values());
        return "apprenants/form";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@Valid @ModelAttribute("apprenant") Apprenant apprenant,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("niveaux", Niveau.values());
            return "apprenants/form";
        }
        service.save(apprenant);
        ra.addFlashAttribute("message", "Apprenant enregistré avec succès.");
        return "redirect:/apprenants";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "Apprenant supprimé.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/apprenants";
    }
}
