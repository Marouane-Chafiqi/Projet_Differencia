package projet.differencia.ma.service;

import org.junit.jupiter.api.Test;
import projet.differencia.ma.entities.Activite;
import projet.differencia.ma.entities.Niveau;
import projet.differencia.ma.repository.ActiviteRepository;
import projet.differencia.ma.repository.AffectationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActiviteServiceTest {

    private final ActiviteRepository repo = mock(ActiviteRepository.class);
    private final AffectationRepository affectationRepo = mock(AffectationRepository.class);
    private final ActiviteService service = new ActiviteService(repo, affectationRepo);

    private Activite activite(String titre, String discipline) {
        Activite a = new Activite();
        a.setTitre(titre);
        a.setDiscipline(discipline);
        return a;
    }

    @Test
    void suggereesRetourneLesActivitesDuNiveauDeLApprenant() {
        List<Activite> liste = List.of(activite("Additions simples", "Maths"));
        when(repo.findByNiveauDifficulte(Niveau.DEBUTANT)).thenReturn(liste);

        assertEquals(liste, service.suggerees(Niveau.DEBUTANT));
    }

    @Test
    void supprimerRefuseSiLActiviteEstDejaAffectee() {
        when(affectationRepo.existsByActiviteId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.delete(1L));
        verify(repo, never()).deleteById(1L);
    }

    @Test
    void rechercherFiltreSurLeTitreOuLaDiscipline() {
        when(repo.findAll()).thenReturn(List.of(
                activite("Additions simples", "Mathématiques"),
                activite("Lecture guidée", "Français")));

        assertEquals(1, service.rechercher("addition", null).size());
        assertEquals(1, service.rechercher("français", null).size());
        assertEquals(2, service.rechercher("", null).size());
    }
}
