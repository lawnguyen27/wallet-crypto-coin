package vn.sparkminds.template.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.sparkminds.template.util.StringUtils;

@Slf4j
@Configuration
public class SwaggerConfiguration {

    private static final String SERVICE_NAME_SUFFIX = "-service";

    @Bean
    public OpenAPI openAPI(@Value("${spring.application.name}") String serviceName) {

        if (StringUtils.isBlank(serviceName)) {
            throw new IllegalArgumentException("Service name is empty");
        }

        if (serviceName.endsWith(SERVICE_NAME_SUFFIX)) {
            serviceName = serviceName.replace(SERVICE_NAME_SUFFIX, StringUtils.EMPTY);
        }

        final Info info = new Info().title("%s Service".formatted(WordUtils.capitalize(serviceName)));

        return new OpenAPI().info(info);
    }
}
