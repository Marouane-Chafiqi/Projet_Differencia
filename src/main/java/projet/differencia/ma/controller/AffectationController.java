package projet.differencia.ma.controller;

import projet.differencia.ma.entities.Apprenant;
import projet.differencia.ma.entities.Statut;
import projet.differencia.ma.service.ActiviteService;
import projet.differencia.ma.service.AffectationService;
import projet.differencia.ma.service.ApprenantService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/affectations")
public class AffectationController {

    private final AffectationService service;
    private final ApprenantService apprenantService;
    private final ActiviteService activiteService;

    public AffectationController(AffectationService service, ApprenantService apprenantService,
                                 ActiviteService activiteService) {
        this.service = service;
        this.apprenantService = apprenantService;
        this.activiteService = activiteService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) Statut statut,
                        @RequestParam(required = false) String q, Model model) {
        model.addAttribute("affectations", service.rechercher(statut, q));
        model.addAttribute("statuts", Statut.values());
        model.addAttribute("statutChoisi", statut);
        model.addAttribute("q", q);
        return "affectations/liste";
    }

    @GetMapping("/nouvelle")
    public String nouvelle(@RequestParam(required = false) Long apprenantId, Model model) {
        model.addAttribute("apprenants", apprenantService.findAll());
        if (apprenantId != null) {
            Apprenant ap = apprenantService.findById(apprenantId);
            model.addAttribute("apprenant", ap);
            model.addAttribute("suggerees", activiteService.suggerees(ap.getNiveauActuel()));
            model.addAttribute("autres", activiteService.autresNiveaux(ap.getNiveauActuel()));
            model.addAttribute("dejaAffectees", service.activitesNonTerminees(apprenantId));
            model.addAttribute("dateParDefaut", LocalDate.now().plusDays(7));
            model.addAttribute("aujourdhui", LocalDate.now());
        }
        return "affectations/nouvelle";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@RequestParam Long apprenantId,
                              @RequestParam(required = false) Long activiteId,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateLimite,
                              RedirectAttributes ra) {
        try {
            service.affecter(apprenantId, activiteId, dateLimite);
            ra.addFlashAttribute("message", "Activité affectée avec succès.");
            return "redirect:/affectations";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/affectations/nouvelle?apprenantId=" + apprenantId;
        }
    }

    @GetMapping("/{id}/suivi")
    public String suivi(@PathVariable Long id, Model model) {
        model.addAttribute("affectation", service.findById(id));
        model.addAttribute("statuts", Statut.values());
        return "affectations/suivi";
    }

    @PostMapping("/{id}/suivi")
    public String enregistrerSuivi(@PathVariable Long id,
                                   @RequestParam Statut statut,
                                   @RequestParam(required = false) Integer resultat,
                                   @RequestParam(required = false) String commentaire,
                                   RedirectAttributes ra) {
        try {
            service.mettreAJour(id, statut, resultat, commentaire);
            ra.addFlashAttribute("message", "Suivi mis à jour.");
            return "redirect:/affectations";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/affectations/" + id + "/suivi";
        }
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        service.supprimer(id);
        ra.addFlashAttribute("message", "Affectation supprimée.");
        return "redirect:/affectations";
    }
}
