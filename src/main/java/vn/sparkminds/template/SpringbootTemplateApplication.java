package vn.sparkminds.template;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import vn.sparkminds.template.config.ApplicationProperties;
import vn.sparkminds.template.job.AdminJob;

import java.util.Collections;
import java.util.Properties;

@AllArgsConstructor
@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class SpringbootTemplateApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootTemplateApplication.class, args);

    }
}