package projet.differencia.ma.config;

import projet.differencia.ma.entities.*;
import projet.differencia.ma.repository.ActiviteRepository;
import projet.differencia.ma.repository.AffectationRepository;
import projet.differencia.ma.repository.ApprenantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final ApprenantRepository apprenantRepo;
    private final ActiviteRepository activiteRepo;
    private final AffectationRepository affectationRepo;

    public DataLoader(ApprenantRepository apprenantRepo, ActiviteRepository activiteRepo,
                      AffectationRepository affectationRepo) {
        this.apprenantRepo = apprenantRepo;
        this.activiteRepo = activiteRepo;
        this.affectationRepo = affectationRepo;
    }

    @Override
    public void run(String... args) {
        if (apprenantRepo.count() > 0) return;

        Apprenant marouane = apprenant("Chafiqi", "Marouane", "G1", Niveau.DEBUTANT);
        Apprenant walid = apprenant("Aarkab", "Walid", "G1", Niveau.DEBUTANT);
        Apprenant imane = apprenant("Tazi", "Imane", "G1", Niveau.DEBUTANT);
        Apprenant simo = apprenant("Khalloufi", "Simo", "G2", Niveau.INTERMEDIAIRE);
        Apprenant karim = apprenant("Hani", "Karim", "G2", Niveau.INTERMEDIAIRE);
        Apprenant said = apprenant("Amali", "Said", "G2", Niveau.INTERMEDIAIRE);
        Apprenant hamza = apprenant("Benkirane", "Hamza", "G2", Niveau.INTERMEDIAIRE);
        Apprenant malak = apprenant("Najem", "Malak", "G3", Niveau.AVANCE);
        Apprenant samira = apprenant("Wafi", "Samira", "G3", Niveau.AVANCE);
        Apprenant anas = apprenant("Idrissi", "Anas", "G3", Niveau.AVANCE);

        Activite a1 = activite("Additions simples", "Mathématiques", "Maîtriser l'addition", "Résoudre 10 additions.", Niveau.DEBUTANT, 20);
        Activite a2 = activite("Lecture guidée", "Français", "Lire un texte court", "Lire et répondre à 5 questions.", Niveau.DEBUTANT, 25);
        Activite a3 = activite("Équations du 1er degré", "Mathématiques", "Résoudre une équation", "Résoudre 8 équations.", Niveau.INTERMEDIAIRE, 40);
        Activite a4 = activite("Rédaction d'un paragraphe", "Français", "Structurer un texte", "Écrire un paragraphe de 8 lignes.", Niveau.INTERMEDIAIRE, 35);
        Activite a5 = activite("Problème ouvert", "Mathématiques", "Raisonner et justifier", "Résoudre un problème complexe.", Niveau.AVANCE, 60);
        Activite a6 = activite("Analyse de texte", "Français", "Analyser un texte littéraire", "Rédiger une analyse de 1 page.", Niveau.AVANCE, 50);
        Activite a7 = activite("Mini-projet sciences", "Sciences", "Mener une démarche scientifique", "Réaliser une expérience.", Niveau.INTERMEDIAIRE, 90);
        Activite a8 = activite("Vocabulaire de base", "Anglais", "Apprendre 20 mots", "Associer mots et images.", Niveau.DEBUTANT, 15);

        affectation(marouane, a1, Statut.TERMINE, 85, "Très bien");
        affectation(marouane, a8, Statut.EN_COURS, null, null);
        affectation(walid, a2, Statut.TERMINE, 60, "Doit s'entraîner");
        affectation(imane, a2, Statut.TERMINE, 70, "Bon effort");
        affectation(simo, a3, Statut.TERMINE, 75, "Bon travail");
        Affectation enRetard = affectation(karim, a4, Statut.A_FAIRE, null, null);
        enRetard.setDateLimite(LocalDate.now().minusDays(2));
        affectationRepo.save(enRetard);
        affectation(said, a7, Statut.EN_COURS, null, null);
        affectation(hamza, a4, Statut.TERMINE, 68, "Peut mieux structurer");
        affectation(malak, a5, Statut.TERMINE, 92, "Excellent");
        affectation(samira, a3, Statut.EN_COURS, null, "Activité d'un niveau inférieur (révision)");
        affectation(anas, a6, Statut.TERMINE, 88, "Très bonne analyse");
    }

    private Apprenant apprenant(String nom, String prenom, String groupe, Niveau niveau) {
        Apprenant a = new Apprenant();
        a.setNom(nom);
        a.setPrenom(prenom);
        a.setGroupe(groupe);
        a.setNiveauActuel(niveau);
        return apprenantRepo.save(a);
    }

    private Activite activite(String titre, String discipline, String objectif, String consignes, Niveau niveau, int duree) {
        Activite a = new Activite();
        a.setTitre(titre);
        a.setDiscipline(discipline);
        a.setObjectifPedagogique(objectif);
        a.setConsignes(consignes);
        a.setNiveauDifficulte(niveau);
        a.setDureeEstimee(duree);
        return activiteRepo.save(a);
    }

    private Affectation affectation(Apprenant apprenant, Activite activite, Statut statut, Integer resultat, String commentaire) {
        Affectation a = new Affectation();
        a.setApprenant(apprenant);
        a.setActivite(activite);
        a.setDateAffectation(LocalDate.now().minusDays(5));
        a.setDateLimite(LocalDate.now().plusDays(7));
        a.setStatut(statut);
        a.setResultat(resultat);
        a.setCommentaire(commentaire);
        return affectationRepo.save(a);
    }
}
