package projet.differencia.ma.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "apprenant_id")
    private Apprenant apprenant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "activite_id")
    private Activite activite;

    @Column(name = "date_affectation")
    private LocalDate dateAffectation;

    @Column(name = "date_limite")
    private LocalDate dateLimite;

    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.A_FAIRE;

    @Min(0) @Max(100)
    private Integer resultat;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    public boolean isEnRetard() {
        return statut != Statut.TERMINE && dateLimite != null && dateLimite.isBefore(LocalDate.now());
    }
}
