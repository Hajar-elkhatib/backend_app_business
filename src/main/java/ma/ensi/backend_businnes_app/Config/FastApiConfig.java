package ma.ensi.backend_businnes_app.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class FastApiConfig {

    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public String fastApiBaseUrl() {
        return fastApiBaseUrl;
    }
}