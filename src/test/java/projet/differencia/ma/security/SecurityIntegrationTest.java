package projet.differencia.ma.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import projet.differencia.ma.entities.Role;
import projet.differencia.ma.service.UtilisateurService;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired UtilisateurService utilisateurService;
    @Autowired LoginAttemptService tentatives;

    @AfterEach
    void nettoyer() {
        tentatives.enregistrerSucces("prof");
        tentatives.enregistrerSucces("admin");
    }

    @Test
    void anonymeEstRedirigeVersLaPageDeConnexion() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        mvc.perform(get("/apprenants"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void laPageDeConnexionEstPublique() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Se connecter")));
    }

    @Test
    void connexionValideDonneAccesAuTableauDeBord() throws Exception {
        mvc.perform(formLogin("/login").user("admin").password("Admin@12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("admin"));
    }

    @Test
    void mauvaisMotDePasseEstRefuse() throws Exception {
        mvc.perform(formLogin("/login").user("prof").password("faux"))
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void compteVerrouilleApresCinqEchecs() throws Exception {
        String nom = "verrou" + System.nanoTime();
        utilisateurService.creer(nom, "Compte Test", "Test1234", Role.ENSEIGNANT);

        for (int i = 0; i < LoginAttemptService.MAX_ECHECS; i++) {
            mvc.perform(formLogin("/login").user(nom).password("mauvais"))
                    .andExpect(redirectedUrl("/login?error"));
        }
        // Même avec le bon mot de passe, le compte est verrouillé
        mvc.perform(formLogin("/login").user(nom).password("Test1234"))
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @WithMockUser(roles = "ENSEIGNANT")
    void enseignantNePeutPasGererLesUtilisateurs() throws Exception {
        mvc.perform(get("/utilisateurs")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ENSEIGNANT")
    void enseignantNePeutPasSupprimer() throws Exception {
        mvc.perform(post("/apprenants/1/supprimer").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ENSEIGNANT")
    void enseignantNeVoitPasLesBoutonsDeSuppression() throws Exception {
        mvc.perform(get("/apprenants"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("/supprimer"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminVoitLesBoutonsDeSuppressionEtLaListeDesUtilisateurs() throws Exception {
        mvc.perform(get("/apprenants"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/supprimer")));
        mvc.perform(get("/utilisateurs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Utilisateurs")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSansJetonCsrfEstRefuse() throws Exception {
        mvc.perform(post("/apprenants/enregistrer")).andExpect(status().isForbidden());
    }

    @Test
    void changementDeMotDePasse() throws Exception {
        String nom = "chg" + System.nanoTime();
        utilisateurService.creer(nom, "Compte Test", "Ancien1234", Role.ENSEIGNANT);

        mvc.perform(post("/profil/mot-de-passe")
                        .with(user(nom).roles("ENSEIGNANT")).with(csrf())
                        .param("ancien", "Ancien1234")
                        .param("nouveau", "Nouveau1234")
                        .param("confirmation", "Nouveau1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mvc.perform(formLogin("/login").user(nom).password("Nouveau1234"))
                .andExpect(authenticated().withUsername(nom));
    }

    @Test
    void deconnexionSeFaitEnPostEtRedirigeVersLogin() throws Exception {
        mvc.perform(post("/logout").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }
}
