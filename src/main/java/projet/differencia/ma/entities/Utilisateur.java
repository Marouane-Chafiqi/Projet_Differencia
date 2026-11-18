package projet.differencia.ma.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "utilisateur", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@Getter @Setter
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    /** Mot de passe haché avec BCrypt (jamais en clair). */
    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "nom_complet", nullable = false, length = 100)
    private String nomComplet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean actif = true;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    public String getDateCreationFormatee() {
        return dateCreation == null ? "" : dateCreation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
