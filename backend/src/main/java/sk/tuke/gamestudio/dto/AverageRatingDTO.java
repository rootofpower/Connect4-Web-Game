package sk.tuke.gamestudio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageRatingDTO {
    private String game;
    private double averageRating;
    private long ratingCount;
}