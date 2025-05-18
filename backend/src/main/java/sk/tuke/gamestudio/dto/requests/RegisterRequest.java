package sk.tuke.gamestudio.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username can't be empty!")
    @Size(min = 3, max = 50, message = "Username length must be longer than 3 and shorter than 50!")
    private String username;

    @NotBlank(message = "Email can't be empty!")
    @Email(message = "Bad email format!")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Password can't be empty!")
    @Size(min = 8, message = "Password can't be shorter than 8!")
    private String password;
}
