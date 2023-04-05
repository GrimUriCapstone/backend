package grimuri.backend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "GrimUri Backend API Docs",
                description = "KHU-CSE 2023학년도 1학기 캡스톤디자인 GrimUri",
                version = "v1"
        )
)
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi grimUriOpenApi() {
        String[] paths = {"/api/v1/**"};

        return GroupedOpenApi.builder()
                .group("GrimUri API v1")
                .pathsToMatch(paths)
                .build();
    }
}
