package vn.sparkminds.template.config.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import vn.sparkminds.template.util.StringUtils;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSourceService {

    private final MessageSource messageSource;

    public String getMessage(String code) {
        try {
            String message = messageSource.getMessage(code, null, Locale.ENGLISH);

            if (StringUtils.isBlank(message)) {
                return code;
            }

            return message;
        } catch (NoSuchMessageException e) {
            return code;
        }
    }

    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.ENGLISH);
    }

}
