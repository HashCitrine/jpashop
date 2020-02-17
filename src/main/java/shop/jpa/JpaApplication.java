package shop.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class JpaApplication {

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.yml,"
            + "/app/config/db.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(JpaApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }
}