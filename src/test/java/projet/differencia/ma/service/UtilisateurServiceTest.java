package projet.differencia.ma.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import projet.differencia.ma.entities.Role;
import projet.differencia.ma.entities.Utilisateur;
import projet.differencia.ma.repository.UtilisateurRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UtilisateurServiceTest {

    private final UtilisateurRepository repo = mock(UtilisateurRepository.class);
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UtilisateurService service = new UtilisateurService(repo, encoder);

    private Utilisateur utilisateur(Long id, String username) {
        Utilisateur u = new Utilisateur();
        u.setId(id);
        u.setUsername(username);
        u.setNomComplet(username);
        u.setPassword(encoder.encode("Secret123"));
        u.setRole(Role.ENSEIGNANT);
        return u;
    }

    @Test
    void creerHacheLeMotDePasse() {
        when(repo.existsByUsernameIgnoreCase("sara")).thenReturn(false);
        when(repo.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        Utilisateur u = service.creer(" sara ", "Sara A.", "Secret123", Role.ENSEIGNANT);

        assertEquals("sara", u.getUsername());
        assertNotEquals("Secret123", u.getPassword());
        assertTrue(encoder.matches("Secret123", u.getPassword()));
    }

    @Test
    void creerRefuseUnNomDejaUtilise() {
        when(repo.existsByUsernameIgnoreCase("admin")).thenReturn(true);
        assertThrows(IllegalStateException.class,
                () -> service.creer("admin", "Autre", "Secret123", Role.ADMIN));
        verify(repo, never()).save(any());
    }

    @Test
    void supprimerRefuseSonPropreCompte() {
        when(repo.findById(1L)).thenReturn(Optional.of(utilisateur(1L, "admin")));
        assertThrows(IllegalStateException.class, () -> service.supprimer(1L, "ADMIN"));
        verify(repo, never()).delete(any());
    }

    @Test
    void desactiverRefuseSonPropreCompte() {
        when(repo.findById(1L)).thenReturn(Optional.of(utilisateur(1L, "admin")));
        assertThrows(IllegalStateException.class, () -> service.basculerActif(1L, "admin"));
    }

    @Test
    void changerMotDePasseExigeLAncienMotDePasse() {
        when(repo.findByUsernameIgnoreCase("sara")).thenReturn(Optional.of(utilisateur(2L, "sara")));
        assertThrows(IllegalStateException.class,
                () -> service.changerMotDePasse("sara", "faux", "Nouveau123"));
        verify(repo, never()).save(any());
    }
}
