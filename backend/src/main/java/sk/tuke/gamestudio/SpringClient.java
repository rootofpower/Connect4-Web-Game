package sk.tuke.gamestudio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import sk.tuke.gamestudio.game.connectfour.MainMenu;
import sk.tuke.gamestudio.game.connectfour.core.GameController;
import sk.tuke.gamestudio.game.connectfour.ui.ConsoleUI;
import sk.tuke.gamestudio.game.connectfour.ui.UserInterface;
// import org.springframework.web.client.RestTemplate;
// import sk.tuke.gamestudio.service.*;
// import sk.tuke.gamestudio.service.JPA.CommentServiceJPA;
// import sk.tuke.gamestudio.service.JPA.RatingServiceJPA;
// import sk.tuke.gamestudio.service.JPA.ScoreServiceJPA;
// import sk.tuke.gamestudio.security.SecurityConfig;
// import sk.tuke.gamestudio.service.RestClient.CommentServiceRestClient;
// import sk.tuke.gamestudio.service.RestClient.RatingServiceRestClient;
// import sk.tuke.gamestudio.service.RestClient.ScoreServiceRestClient;
import sk.tuke.gamestudio.service.interfaces.CommentService;
import sk.tuke.gamestudio.service.interfaces.RatingService;
import sk.tuke.gamestudio.service.interfaces.ScoreService;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan(basePackages = { "sk.tuke.gamestudio" }, // Вказуємо базовий пакет для сканування
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "sk.tuke.gamestudio.server.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "sk.tuke.gamestudio.service.JPA.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "sk.tuke.gamestudio.security.*")
        })
public class SpringClient {
    public static void main(String[] args) {
        // SpringApplication.run(SpringClient.class);
        new SpringApplicationBuilder(SpringClient.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner runner(MainMenu menu, UserInterface ui, GameController gc) {
        return args -> menu.startMenu(ui, gc);
    }

    @Bean
    public UserInterface ui() {
        return new ConsoleUI();
    }

    @Bean
    public GameController gameController() {
        return new GameController(2);
    }

    // @Bean
    // public CommentService commentService() {
    // // return new CommentServiceJDBC();
    // // return new CommentServiceJPA();
    // return new CommentServiceRestClient();
    // }

    // @Bean
    // public RatingService ratingService() {
    // // return new RatingServiceJDBC();
    // // return new RatingServiceJPA();
    // return new RatingServiceRestClient();
    // }

    // @Bean
    // public RestTemplate restTemplate() {
    // return new RestTemplate();
    // }

    // @Bean
    // public ScoreService scoreService() {
    // // return new ScoreServiceJDBC();
    // // return new ScoreServiceJPA();
    // return new ScoreServiceRestClient();
    // }
}
