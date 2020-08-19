package no.nav.registre.syntrest.consumer;

import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriTemplate;

public class UriExpander {

    public static RequestEntity createRequestEntity(String url, String code, int numToGenerate) {
        return RequestEntity.get(new UriTemplate(url).expand(numToGenerate, code)).build();
    }

    public static RequestEntity createRequestEntity(String url, int numToGenerate) {
        return RequestEntity.get(new UriTemplate(url).expand(numToGenerate)).build();
    }

    public static RequestEntity createRequestEntity(String url, Object body) {
        return RequestEntity.post(new UriTemplate(url).expand()).body(body);
    }

}
