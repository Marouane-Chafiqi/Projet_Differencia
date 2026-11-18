package projet.differencia.ma.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Apprenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "Le groupe est obligatoire")
    private String groupe;

    @NotNull(message = "Le niveau est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_actuel")
    private Niveau niveauActuel;
}
