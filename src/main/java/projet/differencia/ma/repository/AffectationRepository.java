package projet.differencia.ma.repository;

import projet.differencia.ma.entities.Affectation;
import projet.differencia.ma.entities.Statut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AffectationRepository extends JpaRepository<Affectation, Long> {

    boolean existsByApprenantIdAndActiviteIdAndStatutNot(Long apprenantId, Long activiteId, Statut statut);

    boolean existsByApprenantIdAndActiviteIdAndStatutNotAndIdNot(Long apprenantId, Long activiteId, Statut statut, Long id);

    boolean existsByApprenantId(Long apprenantId);

    boolean existsByActiviteId(Long activiteId);

    long countByStatut(Statut statut);

    List<Affectation> findByApprenantId(Long apprenantId);

    List<Affectation> findByStatutNotAndDateLimiteBefore(Statut statut, LocalDate date);

    List<Affectation> findTop5ByOrderByIdDesc();
}
