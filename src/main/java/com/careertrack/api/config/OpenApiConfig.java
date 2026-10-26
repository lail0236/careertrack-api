
package com.careertrack.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI careerTrackOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CareerTrack API")
                        .version("1.0.0")
                        .description(
                                "A job application tracking API with "
                                + "status history and recruiting analytics."
                        )
                        
                );
    }
}
