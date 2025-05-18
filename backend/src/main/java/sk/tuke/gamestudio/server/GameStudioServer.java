package sk.tuke.gamestudio.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "sk.tuke.gamestudio.entity")
@EnableJpaRepositories(basePackages = "sk.tuke.gamestudio.repository")
@ComponentScan(basePackages = {
        // "sk.tuke.gamestudio.security",
        // "sk.tuke.gamestudio.service.JPA",
        // "sk.tuke.gamestudio.mapper",
        // "sk.tuke.gamestudio.server",
        // "sk.tuke.gamestudio.dto"
        "sk.tuke.gamestudio.*"
})
// swagger: http://localhost:8080/swagger-ui/index.html
// openapi: http://localhost:8080/v3/api-docs
// http://localhost:8080/v3/api-docs.yaml
public class GameStudioServer {
    public static void main(String[] args) {
        SpringApplication.run(GameStudioServer.class);
    }
}
