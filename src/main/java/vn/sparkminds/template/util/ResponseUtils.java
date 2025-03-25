package vn.sparkminds.template.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import vn.sparkminds.template.config.error.http.NotFoundException;

import java.text.MessageFormat;
import java.util.Optional;

public final class ResponseUtils {

    private static final String X_TOTAL_COUNT = "X-Total-Count";
    private static final String LINK = "Link";
    private static final String LINK_FORMAT = "<{0}>; rel=\"{1}\"";
    private static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    private ResponseUtils() {}

    public static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse) {
        return maybeResponse
                .map(response -> ResponseEntity.ok().body(response))
                .orElseThrow(NotFoundException::new);
    }

    public static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse, HttpHeaders header) {
        return maybeResponse
                .map(response -> ResponseEntity.ok().headers(header).body(response))
                .orElseThrow(NotFoundException::new);
    }

    public static <T> HttpHeaders generatePaginationHeaders(UriComponentsBuilder uriBuilder, Page<T> page) {
        HttpHeaders headers = new HttpHeaders();

        headers.add(X_TOTAL_COUNT, Long.toString(page.getTotalElements()));

        final int pageNumber = page.getNumber();
        final int pageSize = page.getSize();
        final StringBuilder link = new StringBuilder();

        if (pageNumber < page.getTotalPages() - 1) {
            link.append(prepareLink(uriBuilder, pageNumber + 1, pageSize, "next")).append(",");
        }

        if (pageNumber > 0) {
            link.append(prepareLink(uriBuilder, pageNumber - 1, pageSize, "prev")).append(",");
        }

        link.append(prepareLink(uriBuilder, page.getTotalPages() - 1, pageSize, "last"))
                .append(",")
                .append(prepareLink(uriBuilder, 0, pageSize, "first"));

        headers.add(LINK, link.toString());
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, "*");

        return headers;
    }

    private static String prepareLink(UriComponentsBuilder uriBuilder, int pageNumber, int pageSize, String relType) {
        return MessageFormat.format(LINK_FORMAT, preparePageUri(uriBuilder, pageNumber, pageSize), relType);
    }

    private static String preparePageUri(UriComponentsBuilder uriBuilder, int pageNumber, int pageSize) {
        return uriBuilder
                .replaceQueryParam("page", Integer.toString(pageNumber))
                .replaceQueryParam("size", Integer.toString(pageSize))
                .toUriString()
                .replace(",", "%2C")
                .replace(";", "%3B");
    }
}
