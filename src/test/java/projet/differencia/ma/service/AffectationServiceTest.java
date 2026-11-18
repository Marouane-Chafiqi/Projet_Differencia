package projet.differencia.ma.service;

import org.junit.jupiter.api.Test;
import projet.differencia.ma.entities.Activite;
import projet.differencia.ma.entities.Affectation;
import projet.differencia.ma.entities.Apprenant;
import projet.differencia.ma.entities.Niveau;
import projet.differencia.ma.entities.Statut;
import projet.differencia.ma.repository.AffectationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Tests unitaires des règles de gestion (sans base de données : on "simule" les repositories avec Mockito)
class AffectationServiceTest {

    private final AffectationRepository repo = mock(AffectationRepository.class);
    private final ApprenantService apprenantService = mock(ApprenantService.class);
    private final ActiviteService activiteService = mock(ActiviteService.class);
    private final AffectationService service = new AffectationService(repo, apprenantService, activiteService);

    private Apprenant apprenant(Long id, Niveau niveau) {
        Apprenant a = new Apprenant();
        a.setId(id);
        a.setNiveauActuel(niveau);
        return a;
    }

    private Activite activite(Long id) {
        Activite a = new Activite();
        a.setId(id);
        return a;
    }

    private Affectation affectation(Long id, Apprenant apprenant, Activite activite, Statut statut, Integer resultat) {
        Affectation a = new Affectation();
        a.setId(id);
        a.setApprenant(apprenant);
        a.setActivite(activite);
        a.setStatut(statut);
        a.setResultat(resultat);
        return a;
    }

    // ---------- affecter() ----------

    @Test
    void affecterEnregistreUneAffectationValide() {
        when(repo.existsByApprenantIdAndActiviteIdAndStatutNot(1L, 2L, Statut.TERMINE)).thenReturn(false);
        when(apprenantService.findById(1L)).thenReturn(apprenant(1L, Niveau.DEBUTANT));
        when(activiteService.findById(2L)).thenReturn(activite(2L));

        service.affecter(1L, 2L, LocalDate.now().plusDays(3));

        verify(repo).save(any(Affectation.class));
    }

    @Test
    void affecterRefuseUnDoublonNonTermine() {
        when(repo.existsByApprenantIdAndActiviteIdAndStatutNot(1L, 2L, Statut.TERMINE)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.affecter(1L, 2L, LocalDate.now().plusDays(3)));
        verify(repo, never()).save(any(Affectation.class));
    }

    @Test
    void affecterRefuseUneDateLimiteDansLePasse() {
        assertThrows(IllegalArgumentException.class,
                () -> service.affecter(1L, 2L, LocalDate.now().minusDays(1)));
        verify(repo, never()).save(any(Affectation.class));
    }

    // ---------- mettreAJour() ----------

    @Test
    void mettreAJourRefuseUnResultatSuperieurA100() {
        assertThrows(IllegalArgumentException.class,
                () -> service.mettreAJour(5L, Statut.TERMINE, 120, "ok"));
        verify(repo, never()).save(any(Affectation.class));
    }

    @Test
    void mettreAJourRefuseTermineSansResultat() {
        assertThrows(IllegalArgumentException.class,
                () -> service.mettreAJour(5L, Statut.TERMINE, null, "ok"));
        verify(repo, never()).save(any(Affectation.class));
    }

    @Test
    void mettreAJourRefuseDeRouvrirSiLaMemeActiviteEstDejaEnCours() {
        Affectation a = affectation(5L, apprenant(1L, Niveau.DEBUTANT), activite(2L), Statut.TERMINE, 90);
        when(repo.findById(5L)).thenReturn(Optional.of(a));
        when(repo.existsByApprenantIdAndActiviteIdAndStatutNotAndIdNot(1L, 2L, Statut.TERMINE, 5L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.mettreAJour(5L, Statut.EN_COURS, null, null));
        verify(repo, never()).save(any(Affectation.class));
    }

    @Test
    void mettreAJourEffaceLeResultatSiLActiviteNEstPasTerminee() {
        Affectation a = affectation(5L, apprenant(1L, Niveau.DEBUTANT), activite(2L), Statut.TERMINE, 90);
        when(repo.findById(5L)).thenReturn(Optional.of(a));
        when(repo.existsByApprenantIdAndActiviteIdAndStatutNotAndIdNot(1L, 2L, Statut.TERMINE, 5L)).thenReturn(false);

        service.mettreAJour(5L, Statut.EN_COURS, 50, "en cours");

        assertEquals(Statut.EN_COURS, a.getStatut());
        assertNull(a.getResultat());
        verify(repo).save(a);
    }

    // ---------- statistiques ----------

    @Test
    void statistiquesParNiveauCalculeTotalTermineesEtMoyenne() {
        Apprenant debutant = apprenant(1L, Niveau.DEBUTANT);
        Apprenant avance = apprenant(2L, Niveau.AVANCE);
        Activite act = activite(1L);
        when(repo.findAll()).thenReturn(List.of(
                affectation(1L, debutant, act, Statut.TERMINE, 80),
                affectation(2L, debutant, act, Statut.TERMINE, 60),
                affectation(3L, avance, act, Statut.EN_COURS, null)));

        List<StatNiveau> stats = service.statistiquesParNiveau();

        StatNiveau deb = stats.get(0);
        assertEquals(Niveau.DEBUTANT, deb.getNiveau());
        assertEquals(2L, deb.getTotal());
        assertEquals(2L, deb.getTermines());
        assertEquals(70.0, deb.getMoyenne(), 0.001);

        StatNiveau adv = stats.get(2);
        assertEquals(Niveau.AVANCE, adv.getNiveau());
        assertEquals(1L, adv.getTotal());
        assertEquals(0L, adv.getTermines());
        assertEquals(0.0, adv.getMoyenne(), 0.001);
    }
}
