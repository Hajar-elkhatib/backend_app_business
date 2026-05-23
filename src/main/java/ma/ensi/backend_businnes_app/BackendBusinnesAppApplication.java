package ma.ensi.backend_businnes_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
@SpringBootApplication
@EnableAsync
public class BackendBusinnesAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendBusinnesAppApplication.class, args);
    }

}
