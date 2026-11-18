package projet.differencia.ma.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projet.differencia.ma.entities.Utilisateur;
import projet.differencia.ma.repository.UtilisateurRepository;

/** Charge l'utilisateur depuis la base de données pour Spring Security. */
@Service
public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository repository;
    private final LoginAttemptService tentatives;

    public UtilisateurDetailsService(UtilisateurRepository repository, LoginAttemptService tentatives) {
        this.repository = repository;
        this.tentatives = tentatives;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur u = repository.findByUsernameIgnoreCase(username == null ? "" : username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole().name())
                .disabled(!u.isActif())
                .accountLocked(tentatives.estVerrouille(u.getUsername()))
                .build();
    }
}
