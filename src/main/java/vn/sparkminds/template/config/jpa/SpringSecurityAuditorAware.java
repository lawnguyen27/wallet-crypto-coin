package vn.sparkminds.template.config.jpa;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS;

@ComponentScan
@Component("springSecurityAuditorAware")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    private static final String SYSTEM = "SYSTEM";
    private static final String GUEST = "GUEST";

    @Nonnull
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SYSTEM);
    }

    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module().configure(SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
    }
}
