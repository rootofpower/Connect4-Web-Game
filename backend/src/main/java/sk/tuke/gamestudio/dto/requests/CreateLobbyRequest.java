package sk.tuke.gamestudio.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateLobbyRequest {
    @NotBlank(message = "Game name cannot be blank")
    private String gameName;
}
