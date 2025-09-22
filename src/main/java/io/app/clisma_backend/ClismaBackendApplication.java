package io.app.clisma_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication
@EntityScan(basePackages = "io.app.clisma_backend.domain")
public class ClismaBackendApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ClismaBackendApplication.class, args);
    }

}
