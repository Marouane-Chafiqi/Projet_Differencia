package projet.differencia.ma.entities;

public enum Niveau {
    DEBUTANT("Débutant"),
    INTERMEDIAIRE("Intermédiaire"),
    AVANCE("Avancé");

    private final String label;
    Niveau(String label) { this.label = label; }
    public String getLabel() { return label; }
}
