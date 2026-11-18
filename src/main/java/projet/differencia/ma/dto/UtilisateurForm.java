package projet.differencia.ma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import projet.differencia.ma.entities.Role;

@Getter @Setter
public class UtilisateurForm {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 30, message = "Entre 3 et 30 caractères")
    @Pattern(regexp = "^[A-Za-z0-9._-]*$", message = "Lettres, chiffres, point, tiret et underscore uniquement")
    private String username;

    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(max = 100, message = "100 caractères maximum")
    private String nomComplet;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, max = 72, message = "Entre 8 et 72 caractères")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$", message = "Au moins une lettre et un chiffre")
    private String password;

    @NotBlank(message = "Veuillez confirmer le mot de passe")
    private String confirmation;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;
}
