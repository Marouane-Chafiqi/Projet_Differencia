package projet.differencia.ma.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import projet.differencia.ma.repository.UtilisateurRepository;

/** Alimente le compteur de tentatives de connexion. */
@Component
public class AuthenticationEvents {

    private final LoginAttemptService tentatives;
    private final UtilisateurRepository repository;

    public AuthenticationEvents(LoginAttemptService tentatives, UtilisateurRepository repository) {
        this.tentatives = tentatives;
        this.repository = repository;
    }

    @EventListener
    public void surEchec(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        // On ne mémorise que les comptes existants (évite de remplir la mémoire avec de faux noms).
        if (username != null && repository.existsByUsernameIgnoreCase(username.trim())) {
            tentatives.enregistrerEchec(username);
        }
    }

    @EventListener
    public void surSucces(AuthenticationSuccessEvent event) {
        tentatives.enregistrerSucces(event.getAuthentication().getName());
    }
}
