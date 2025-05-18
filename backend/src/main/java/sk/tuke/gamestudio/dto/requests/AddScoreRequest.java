package sk.tuke.gamestudio.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddScoreRequest {
    @NotBlank(message = "Game name cannot be blank")
    private String game;

    @PositiveOrZero(message = "Points cannot be negative")
    private int points;
}
