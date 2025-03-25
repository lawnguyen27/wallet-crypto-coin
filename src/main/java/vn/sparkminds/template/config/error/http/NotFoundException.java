package vn.sparkminds.template.config.error.http;

import org.springframework.http.HttpStatus;

import static vn.sparkminds.template.constant.ErrorConstants.ERROR_RESOURCE_NOT_FOUND;

public class NotFoundException extends BaseHttpException {

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND, ERROR_RESOURCE_NOT_FOUND);
    }

}
