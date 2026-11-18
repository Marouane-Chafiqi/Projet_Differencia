package projet.differencia.ma.repository;

import projet.differencia.ma.entities.Activite;
import projet.differencia.ma.entities.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActiviteRepository extends JpaRepository<Activite, Long> {

    List<Activite> findByNiveauDifficulte(Niveau niveau);

    List<Activite> findByNiveauDifficulteNot(Niveau niveau);
}
