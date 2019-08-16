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

import no.nav.inntektstub.domain.rs.RsInntekt;
import no.nav.inntektstub.domain.rs.RsUser;

@Slf4j
@Component
public class InntektstubConsumer {

    public static final ParameterizedTypeReference<Map<String, List<RsInntekt>>> RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, List<RsInntekt>>>() {
    };

    private RestTemplate restTemplate;

    private UriTemplate leggTilInntektUrl;
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
        this.hentTokenUrl = new UriTemplate(inntektstubUrl + "/v1/user");
    }

    public Map<String, List<RsInntekt>> leggInntekterIInntektstub(Map<String, List<RsInntekt>> inntekter) {
        try {
            HttpHeaders headers = getSession();
            headers.add("Content-Type", "application/json");
            HttpEntity<Map<String, List<RsInntekt>>> entity = new HttpEntity<>(inntekter, headers);
            return restTemplate.exchange(leggTilInntektUrl.expand(), HttpMethod.POST, entity, RESPONSE_TYPE).getBody();
        } catch (HttpClientErrorException e) {
            log.warn(e.getMessage(), e);
            throw e;
        }
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
