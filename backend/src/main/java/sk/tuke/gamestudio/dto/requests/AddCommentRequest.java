package sk.tuke.gamestudio.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddCommentRequest {
    @NotBlank
    private String game;

    @NotBlank
    @Size(max = 1000)
    private String comment;
    @NotBlank
    private String username;

}
