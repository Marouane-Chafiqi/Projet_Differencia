package projet.differencia.ma.entities;

public enum Role {
    ADMIN("Administrateur"),
    ENSEIGNANT("Enseignant");

    private final String label;
    Role(String label) { this.label = label; }
    public String getLabel() { return label; }
}
