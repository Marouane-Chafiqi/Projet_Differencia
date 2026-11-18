package projet.differencia.ma.service;

import org.junit.jupiter.api.Test;
import projet.differencia.ma.repository.AffectationRepository;
import projet.differencia.ma.repository.ApprenantRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApprenantServiceTest {

    private final ApprenantRepository repo = mock(ApprenantRepository.class);
    private final AffectationRepository affectationRepo = mock(AffectationRepository.class);
    private final ApprenantService service = new ApprenantService(repo, affectationRepo);

    @Test
    void supprimerRefuseSiLApprenantADesAffectations() {
        when(affectationRepo.existsByApprenantId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.delete(1L));
        verify(repo, never()).deleteById(1L);
    }

    @Test
    void supprimerAutoriseSiAucuneAffectation() {
        when(affectationRepo.existsByApprenantId(1L)).thenReturn(false);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }
}
