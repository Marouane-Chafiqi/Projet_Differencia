package projet.differencia.ma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangerMotDePasseForm {

    @NotBlank(message = "Saisissez votre mot de passe actuel")
    private String ancien;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = 8, max = 72, message = "Entre 8 et 72 caractères")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$", message = "Au moins une lettre et un chiffre")
    private String nouveau;

    @NotBlank(message = "Veuillez confirmer le nouveau mot de passe")
    private String confirmation;
}
