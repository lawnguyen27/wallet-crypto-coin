package vn.sparkminds.template.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private final Security security = new Security();

    @Getter @Setter
    public static class Security {
        private String jwtSecret;
        private long jwtExpirationInSeconds;
    }
}
