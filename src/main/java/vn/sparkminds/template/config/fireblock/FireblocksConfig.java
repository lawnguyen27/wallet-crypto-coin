package vn.sparkminds.template.config.fireblock;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@ConfigurationProperties(prefix = "fireblocks")
@Getter
@Setter
public class FireblocksConfig {
    private String apiKey;
    private String baseUrl;
    private String secretKeyPath;
    private String webHookPublicKey;
    public String getSecretKey() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("fireblocks_secret.key")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Fireblocks secret key file not found in classpath");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error reading Fireblocks secret key", e);
        }
    }
    public PublicKey getPublicKey() throws Exception {
        String publicKeyPEM = webHookPublicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getMimeDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
}
