package projet.differencia.ma.service;

import projet.differencia.ma.entities.Activite;
import projet.differencia.ma.entities.Niveau;
import projet.differencia.ma.repository.ActiviteRepository;
import projet.differencia.ma.repository.AffectationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActiviteService {

    private final ActiviteRepository repo;
    private final AffectationRepository affectationRepo;

    public ActiviteService(ActiviteRepository repo, AffectationRepository affectationRepo) {
        this.repo = repo;
        this.affectationRepo = affectationRepo;
    }

    public List<Activite> findAll(Niveau niveau) {
        if (niveau == null) {
            return repo.findAll();
        }
        return repo.findByNiveauDifficulte(niveau);
    }

    public Activite findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Activité introuvable"));
    }

    public void save(Activite activite) {
        repo.save(activite);
    }

    public void delete(Long id) {
        if (affectationRepo.existsByActiviteId(id)) {
            throw new IllegalStateException("Impossible de supprimer : cette activité est déjà affectée.");
        }
        repo.deleteById(id);
    }

    public List<Activite> suggerees(Niveau niveauApprenant) {
        return repo.findByNiveauDifficulte(niveauApprenant);
    }

    public List<Activite> autresNiveaux(Niveau niveauApprenant) {
        return repo.findByNiveauDifficulteNot(niveauApprenant);
    }

    public List<Activite> rechercher(String q, Niveau niveau) {
        String mot = (q == null) ? "" : q.trim().toLowerCase();
        return findAll(niveau).stream()
                .filter(a -> mot.isEmpty()
                        || a.getTitre().toLowerCase().contains(mot)
                        || a.getDiscipline().toLowerCase().contains(mot))
                .toList();
    }
}
