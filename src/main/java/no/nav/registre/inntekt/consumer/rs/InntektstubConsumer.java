package no.nav.registre.inntekt.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;

import no.nav.registre.inntekt.domain.RsInntekt;
import no.nav.registre.inntekt.domain.RsPerson;
import no.nav.registre.inntekt.domain.RsUser;

@Slf4j
@Component
public class InntektstubConsumer {

    public static final ParameterizedTypeReference<Map<String, List<RsInntekt>>> RESPONSE_TYPE_INNTEKT = new ParameterizedTypeReference<Map<String, List<RsInntekt>>>() {
    };
    public static final ParameterizedTypeReference<List<RsPerson>> RESPONSE_TYPE_PERSON = new ParameterizedTypeReference<List<RsPerson>>() {
    };

    private RestTemplate restTemplate;

    private UriTemplate leggTilInntektUrl;
    private UriTemplate hentEksisterendeIdenterUrl;
    private UriTemplate hentTokenUrl;

    public InntektstubConsumer(@Value("${inntektstub.rest.api.url}") String inntektstubUrl,
            @Value("${testnorges.ida.credential.inntektstub.username}") String username,
            @Value("${testnorges.ida.credential.inntektstub.password}") String password) {
        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        this.restTemplate = new RestTemplate(factory);
        this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
        this.leggTilInntektUrl = new UriTemplate(inntektstubUrl + "/v1/personer/inntekt");
        this.hentEksisterendeIdenterUrl = new UriTemplate(inntektstubUrl + "/v1/personer");
        this.hentTokenUrl = new UriTemplate(inntektstubUrl + "/v1/user");
    }

    public Map<String, List<RsInntekt>> leggInntekterIInntektstub(Map<String, List<RsInntekt>> inntekter) {
        try {
            HttpEntity<Map<String, List<RsInntekt>>> entity = new HttpEntity<>(inntekter, getHeaders());
            return restTemplate.exchange(leggTilInntektUrl.expand(), HttpMethod.POST, entity, RESPONSE_TYPE_INNTEKT).getBody();
        } catch (HttpClientErrorException e) {
            log.warn(e.getMessage(), e);
            throw e;
        }
    }

    public List<RsPerson> hentEksisterendeIdenter() {
        try {
            HttpEntity entity = new HttpEntity<>(getHeaders());
            return restTemplate.exchange(hentEksisterendeIdenterUrl.expand(), HttpMethod.GET, entity, RESPONSE_TYPE_PERSON).getBody();
        } catch (HttpClientErrorException e) {
            log.warn(e.getMessage(), e);
            throw e;
        }
    }

    public HttpHeaders getHeaders() {
        HttpHeaders session = getSession();
        session.add("Content-Type", "application/json");
        return session;
    }

    private HttpHeaders getSession() {
        HttpEntity request = new HttpEntity("", new HttpHeaders());
        //Have to call this twice to get a valid token
        HttpEntity<RsUser> response1 = restTemplate.exchange(hentTokenUrl.expand(), HttpMethod.GET, request, RsUser.class);
        request = new HttpEntity("", getHeaders(response1));
        HttpEntity<RsUser> response = restTemplate.exchange(hentTokenUrl.expand(), HttpMethod.GET, request, RsUser.class);
        return getHeaders(response);
    }

    private HttpHeaders getHeaders(HttpEntity response) {
        HttpHeaders headers = response.getHeaders();
        HttpHeaders headersRet = new HttpHeaders();
        if (!headers.containsKey("Set-Cookie")) {
            return headersRet;
        }
        for (String entry : headers.get("Set-Cookie")) {
            String token = entry.substring(entry.indexOf('=') + 1, entry.indexOf(';'));
            String name = entry.substring(0, entry.indexOf('='));
            if (name.contains("XSRF")) {
                StringBuilder buf = new StringBuilder(name);
                buf.insert(0, "X-");
                headersRet.add(buf.toString(), token);
            }
        }
        return headersRet;
    }
}
