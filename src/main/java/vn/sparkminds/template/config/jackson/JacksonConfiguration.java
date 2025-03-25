package vn.sparkminds.template.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Objects;

@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(javaTimeModule());
        objectMapper.registerModule(jdk8TimeModule());
        objectMapper.registerModule(bigDecimalModule());

        return objectMapper;
    }

    public SimpleModule bigDecimalModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        return module;
    }

    /**
     * Support for Java date and time API.
     * @return the corresponding Jackson module.
     */
    @Bean
    public JavaTimeModule javaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(Instant.class, new CustomInstantSerializer());
        return module;
    }

    @Bean
    public Jdk8Module jdk8TimeModule() {
        return new Jdk8Module();
    }

    public static final class CustomInstantSerializer extends InstantSerializer {
        private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendInstant(6).toFormatter();

        public CustomInstantSerializer()
        {
            super(InstantSerializer.INSTANCE, false, false, FORMATTER);
        }
    }

    public static final class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

            if (Objects.nonNull(value)) {
                gen.writeString(value.toPlainString());
            } else {
                gen.writeNull();
            }
        }
    }

}
