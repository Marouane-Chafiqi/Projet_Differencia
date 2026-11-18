package projet.differencia.ma.service;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projet.differencia.ma.entities.Role;
import projet.differencia.ma.entities.Utilisateur;
import projet.differencia.ma.repository.UtilisateurRepository;

import java.util.List;

@Service
public class UtilisateurService {

    private final UtilisateurRepository repository;
    private final PasswordEncoder encoder;

    public UtilisateurService(UtilisateurRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> findAll() {
        return repository.findAll(Sort.by("username"));
    }

    @Transactional
    public Utilisateur creer(String username, String nomComplet, String motDePasse, Role role) {
        String nom = username.trim();
        if (repository.existsByUsernameIgnoreCase(nom)) {
            throw new IllegalStateException("Ce nom d'utilisateur existe déjà.");
        }
        Utilisateur u = new Utilisateur();
        u.setUsername(nom);
        u.setNomComplet(nomComplet.trim());
        u.setPassword(encoder.encode(motDePasse));
        u.setRole(role);
        return repository.save(u);
    }

    @Transactional
    public void basculerActif(Long id, String usernameCourant) {
        Utilisateur u = trouver(id);
        if (u.getUsername().equalsIgnoreCase(usernameCourant)) {
            throw new IllegalStateException("Vous ne pouvez pas désactiver votre propre compte.");
        }
        u.setActif(!u.isActif());
        repository.save(u);
    }

    @Transactional
    public void supprimer(Long id, String usernameCourant) {
        Utilisateur u = trouver(id);
        if (u.getUsername().equalsIgnoreCase(usernameCourant)) {
            throw new IllegalStateException("Vous ne pouvez pas supprimer votre propre compte.");
        }
        repository.delete(u);
    }

    @Transactional
    public void changerMotDePasse(String username, String ancien, String nouveau) {
        Utilisateur u = repository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        if (!encoder.matches(ancien, u.getPassword())) {
            throw new IllegalStateException("Le mot de passe actuel est incorrect.");
        }
        u.setPassword(encoder.encode(nouveau));
        repository.save(u);
    }

    private Utilisateur trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
    }
}
