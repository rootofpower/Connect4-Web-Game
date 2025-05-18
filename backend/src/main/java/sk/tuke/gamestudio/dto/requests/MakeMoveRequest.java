package sk.tuke.gamestudio.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MakeMoveRequest {
    @JsonProperty("column")
    private Integer column;
}
