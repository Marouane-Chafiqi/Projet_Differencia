package projet.differencia.ma.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import projet.differencia.ma.entities.Role;
import projet.differencia.ma.repository.UtilisateurRepository;
import projet.differencia.ma.service.UtilisateurService;

/** Crée les comptes initiaux au premier démarrage (table utilisateur vide). */
@Component
public class UtilisateurInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UtilisateurInitializer.class);

    private final UtilisateurRepository repository;
    private final UtilisateurService service;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:Admin@12345}")
    private String adminPassword;

    @Value("${app.demo-users:true}")
    private boolean demoUsers;

    public UtilisateurInitializer(UtilisateurRepository repository, UtilisateurService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        service.creer(adminUsername, "Administrateur", adminPassword, Role.ADMIN);
        log.warn("Compte administrateur créé : '{}'. Changez son mot de passe dès la première connexion !", adminUsername);

        if (demoUsers) {
            service.creer("prof", "Enseignant Démo", "Prof@12345", Role.ENSEIGNANT);
            log.warn("Compte de démonstration créé : 'prof' (ENSEIGNANT). Désactivez-le en production (app.demo-users=false).");
        }
    }
}
