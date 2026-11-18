package projet.differencia.ma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projet.differencia.ma.entities.Utilisateur;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
