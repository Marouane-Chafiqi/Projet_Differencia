package projet.differencia.ma.controller;

import projet.differencia.ma.entities.Statut;
import projet.differencia.ma.service.ActiviteService;
import projet.differencia.ma.service.AffectationService;
import projet.differencia.ma.service.ApprenantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ApprenantService apprenantService;
    private final ActiviteService activiteService;
    private final AffectationService affectationService;

    public DashboardController(ApprenantService apprenantService, ActiviteService activiteService,
                               AffectationService affectationService) {
        this.apprenantService = apprenantService;
        this.activiteService = activiteService;
        this.affectationService = affectationService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("nbApprenants", apprenantService.findAll().size());
        model.addAttribute("nbActivites", activiteService.findAll(null).size());
        model.addAttribute("nbAffectations", affectationService.findAll().size());
        model.addAttribute("stats", affectationService.statistiquesParNiveau());
        model.addAttribute("nbAFaire", affectationService.compter(Statut.A_FAIRE));
        model.addAttribute("nbEnCours", affectationService.compter(Statut.EN_COURS));
        model.addAttribute("nbTermines", affectationService.compter(Statut.TERMINE));
        model.addAttribute("moyenneGenerale", affectationService.moyenneGenerale());
        model.addAttribute("enRetard", affectationService.enRetard());
        model.addAttribute("dernieres", affectationService.dernieres());
        return "index";
    }
}
