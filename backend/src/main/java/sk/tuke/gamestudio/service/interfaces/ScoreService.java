package sk.tuke.gamestudio.service.interfaces;

import sk.tuke.gamestudio.dto.ScoreDTO;
import sk.tuke.gamestudio.dto.requests.AddScoreRequest;
import java.util.List;

public interface ScoreService {

    ScoreDTO addScore(String username, AddScoreRequest addScoreRequest);

    List<ScoreDTO> getTopScores(String game);

    void resetScores(String game);
}
