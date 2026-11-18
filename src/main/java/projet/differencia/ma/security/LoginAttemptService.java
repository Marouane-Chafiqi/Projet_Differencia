package projet.differencia.ma.security;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Protection contre la force brute : après {@value #MAX_ECHECS} échecs consécutifs,
 * le compte est verrouillé pendant {@link #DUREE_VERROUILLAGE}.
 * (Stockage en mémoire : suffisant pour une application sur un seul serveur.)
 */
@Service
public class LoginAttemptService {

    public static final int MAX_ECHECS = 5;
    public static final Duration DUREE_VERROUILLAGE = Duration.ofMinutes(15);

    private record Etat(int echecs, Instant verrouilleJusqua) {
        boolean verrouille() {
            return verrouilleJusqua != null && Instant.now().isBefore(verrouilleJusqua);
        }
        boolean expire() {
            return verrouilleJusqua != null && !Instant.now().isBefore(verrouilleJusqua);
        }
    }

    private final Map<String, Etat> etats = new ConcurrentHashMap<>();

    public void enregistrerEchec(String username) {
        if (username == null) return;
        etats.compute(cle(username), (k, e) -> {
            if (e != null && e.verrouille()) return e;
            int n = (e == null || e.expire() ? 0 : e.echecs()) + 1;
            return new Etat(n, n >= MAX_ECHECS ? Instant.now().plus(DUREE_VERROUILLAGE) : null);
        });
    }

    public void enregistrerSucces(String username) {
        if (username != null) etats.remove(cle(username));
    }

    public boolean estVerrouille(String username) {
        if (username == null) return false;
        Etat e = etats.get(cle(username));
        return e != null && e.verrouille();
    }

    private String cle(String username) {
        return username.trim().toLowerCase();
    }
}
