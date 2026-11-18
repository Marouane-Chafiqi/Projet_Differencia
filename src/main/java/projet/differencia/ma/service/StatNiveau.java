package projet.differencia.ma.service;

import projet.differencia.ma.entities.Niveau;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatNiveau {
    private Niveau niveau;
    private long total;
    private long termines;
    private double moyenne;
}
