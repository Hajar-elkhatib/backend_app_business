package ma.ensi.backend_businnes_app.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
    // Spring Boot configure MongoDB automatiquement
    // via application.properties.
    // @EnableMongoAuditing active le @CreatedDate automatique
    // sur tes classes model.
}
