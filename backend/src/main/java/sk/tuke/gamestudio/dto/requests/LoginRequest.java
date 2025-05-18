package sk.tuke.gamestudio.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Identifier can't be empty!")
    private String identifier;

    @NotBlank(message = "Password can't be empty!")
    private String password;

}
