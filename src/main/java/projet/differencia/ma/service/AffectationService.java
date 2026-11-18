package projet.differencia.ma.service;

import projet.differencia.ma.entities.Affectation;
import projet.differencia.ma.entities.Niveau;
import projet.differencia.ma.entities.Statut;
import projet.differencia.ma.repository.AffectationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AffectationService {

    private final AffectationRepository repo;
    private final ApprenantService apprenantService;
    private final ActiviteService activiteService;

    public AffectationService(AffectationRepository repo, ApprenantService apprenantService,
                              ActiviteService activiteService) {
        this.repo = repo;
        this.apprenantService = apprenantService;
        this.activiteService = activiteService;
    }

    public List<Affectation> findAll() {
        return repo.findAll();
    }

    public Affectation findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Affectation introuvable"));
    }

    public void affecter(Long apprenantId, Long activiteId, LocalDate dateLimite) {
        if (apprenantId == null || activiteId == null) {
            throw new IllegalArgumentException("Choisissez un apprenant et une activité.");
        }
        if (dateLimite == null || dateLimite.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date limite doit être aujourd'hui ou une date future.");
        }
        if (repo.existsByApprenantIdAndActiviteIdAndStatutNot(apprenantId, activiteId, Statut.TERMINE)) {
            throw new IllegalArgumentException("Cette activité est déjà affectée à cet apprenant (pas encore terminée).");
        }

        Affectation a = new Affectation();
        a.setApprenant(apprenantService.findById(apprenantId));
        a.setActivite(activiteService.findById(activiteId));
        a.setDateAffectation(LocalDate.now());
        a.setDateLimite(dateLimite);
        a.setStatut(Statut.A_FAIRE);
        repo.save(a);
    }

    public void mettreAJour(Long id, Statut statut, Integer resultat, String commentaire) {
        if (resultat != null && (resultat < 0 || resultat > 100)) {
            throw new IllegalArgumentException("Le résultat doit être compris entre 0 et 100.");
        }
        if (statut == Statut.TERMINE && resultat == null) {
            throw new IllegalArgumentException("Le résultat est obligatoire pour une activité terminée.");
        }
        Affectation a = findById(id);

        // RG5 : on ne peut pas "rouvrir" une affectation si la même activité
        // est déjà en cours (non terminée) pour le même apprenant
        if (statut != Statut.TERMINE
                && repo.existsByApprenantIdAndActiviteIdAndStatutNotAndIdNot(
                        a.getApprenant().getId(), a.getActivite().getId(), Statut.TERMINE, id)) {
            throw new IllegalArgumentException("Cette activité est déjà en cours pour cet apprenant (une autre affectation n'est pas terminée).");
        }

        a.setStatut(statut);
        // Un résultat n'a de sens que pour une activité terminée
        a.setResultat(statut == Statut.TERMINE ? resultat : null);
        a.setCommentaire(commentaire);
        repo.save(a);
    }

    public void supprimer(Long id) {
        repo.deleteById(id);
    }

    public List<StatNiveau> statistiquesParNiveau() {
        List<Affectation> toutes = repo.findAll();
        List<StatNiveau> stats = new ArrayList<>();

        for (Niveau niveau : Niveau.values()) {
            long total = 0, termines = 0, somme = 0, nbResultats = 0;
            for (Affectation a : toutes) {
                if (a.getApprenant().getNiveauActuel() == niveau) {
                    total++;
                    if (a.getStatut() == Statut.TERMINE) termines++;
                    if (a.getResultat() != null) {
                        somme += a.getResultat();
                        nbResultats++;
                    }
                }
            }
            double moyenne = (nbResultats == 0) ? 0 : (double) somme / nbResultats;
            stats.add(new StatNiveau(niveau, total, termines, Math.round(moyenne * 10) / 10.0));
        }
        return stats;
    }

    public List<Affectation> rechercher(Statut statut, String q) {
        String mot = (q == null) ? "" : q.trim().toLowerCase();
        return repo.findAll().stream()
                .filter(a -> statut == null || a.getStatut() == statut)
                .filter(a -> mot.isEmpty()
                        || a.getApprenant().getNom().toLowerCase().contains(mot)
                        || a.getApprenant().getPrenom().toLowerCase().contains(mot)
                        || a.getActivite().getTitre().toLowerCase().contains(mot))
                .sorted(Comparator.comparing((Affectation a) -> a.getStatut() == Statut.TERMINE)
                        .thenComparing(Affectation::getDateLimite, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public List<Affectation> parApprenant(Long apprenantId) {
        return repo.findByApprenantId(apprenantId);
    }

    public Set<Long> activitesNonTerminees(Long apprenantId) {
        return repo.findByApprenantId(apprenantId).stream()
                .filter(a -> a.getStatut() != Statut.TERMINE)
                .map(a -> a.getActivite().getId())
                .collect(Collectors.toSet());
    }

    public long compter(Statut statut) {
        return repo.countByStatut(statut);
    }

    public List<Affectation> enRetard() {
        return repo.findByStatutNotAndDateLimiteBefore(Statut.TERMINE, LocalDate.now());
    }

    public List<Affectation> dernieres() {
        return repo.findTop5ByOrderByIdDesc();
    }

    public double moyenne(List<Affectation> liste) {
        double m = liste.stream()
                .filter(a -> a.getResultat() != null)
                .mapToInt(Affectation::getResultat)
                .average()
                .orElse(0);
        return Math.round(m * 10) / 10.0;
    }

    public double moyenneGenerale() {
        return moyenne(repo.findAll());
    }
}
