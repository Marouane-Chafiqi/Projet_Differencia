package projet.differencia.ma.service;

import projet.differencia.ma.entities.Apprenant;
import projet.differencia.ma.entities.Niveau;
import projet.differencia.ma.repository.AffectationRepository;
import projet.differencia.ma.repository.ApprenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprenantService {

    private final ApprenantRepository repo;
    private final AffectationRepository affectationRepo;

    public ApprenantService(ApprenantRepository repo, AffectationRepository affectationRepo) {
        this.repo = repo;
        this.affectationRepo = affectationRepo;
    }

    public List<Apprenant> findAll() {
        return repo.findAll();
    }

    public Apprenant findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Apprenant introuvable"));
    }

    public void save(Apprenant apprenant) {
        repo.save(apprenant);
    }

    public void delete(Long id) {
        if (affectationRepo.existsByApprenantId(id)) {
            throw new IllegalStateException("Impossible de supprimer : cet apprenant a des activités affectées.");
        }
        repo.deleteById(id);
    }

    public List<Apprenant> rechercher(String q, Niveau niveau) {
        String mot = (q == null) ? "" : q.trim().toLowerCase();
        return repo.findAll().stream()
                .filter(a -> niveau == null || a.getNiveauActuel() == niveau)
                .filter(a -> mot.isEmpty()
                        || a.getNom().toLowerCase().contains(mot)
                        || a.getPrenom().toLowerCase().contains(mot)
                        || a.getGroupe().toLowerCase().contains(mot))
                .toList();
    }
}
