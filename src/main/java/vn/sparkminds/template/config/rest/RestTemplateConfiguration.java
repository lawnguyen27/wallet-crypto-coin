package vn.sparkminds.template.config.rest;

import jakarta.annotation.Nonnull;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .errorHandler(new ResponseErrorCatcher())
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    public static class ResponseErrorCatcher implements ResponseErrorHandler {

        @Override
        public boolean hasError(@Nonnull ClientHttpResponse response) throws IOException {
            return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
        }

        @Override
        public void handleError(@Nonnull URI url, @Nonnull HttpMethod method, @Nonnull ClientHttpResponse response) throws IOException {
            ResponseErrorHandler.super.handleError(url, method, response);
        }
    }

}
