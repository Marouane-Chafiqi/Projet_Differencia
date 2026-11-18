package projet.differencia.ma.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Activite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "La discipline est obligatoire")
    private String discipline;

    @Column(name = "objectif_pedagogique", columnDefinition = "TEXT")
    private String objectifPedagogique;

    @Column(columnDefinition = "TEXT")
    private String consignes;

    @NotNull(message = "Le niveau de difficulté est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_difficulte")
    private Niveau niveauDifficulte;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 1, message = "La durée doit être au moins 1 minute")
    @Column(name = "duree_estimee")
    private Integer dureeEstimee;
}
