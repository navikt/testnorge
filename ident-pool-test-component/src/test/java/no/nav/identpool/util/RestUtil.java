package no.nav.identpool.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class RestUtil {

    public static HttpEntity lagEnkelHttpEntity() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return new HttpEntity(httpHeaders);
    }

}
