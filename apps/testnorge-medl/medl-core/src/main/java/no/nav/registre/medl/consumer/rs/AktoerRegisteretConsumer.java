package no.nav.registre.medl.consumer.rs;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.medl.consumer.rs.response.AktoerResponse;
import no.nav.registre.medl.consumer.rs.response.IdaResponse;

@Component
@Slf4j
public class AktoerRegisteretConsumer {

    private static final ParameterizedTypeReference<Map<String, AktoerResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;
    private final String aktoerUrl;
    private final String tokenProviderUrl;
    private final String username;
    private final String password;

    public AktoerRegisteretConsumer(
            RestTemplate restTemplate,
            @Value("${aktoerregister.api.url}") String aktoerUrl,
            @Value("${ida.token.provider.url}") String tokenProviderUrl,
            @Value("${testnorges.ida.credential.aktoer.username}") String username,
            @Value("${testnorges.ida.credential.aktoer.password}") String password
    ) {
        this.restTemplate = restTemplate;
        this.aktoerUrl = aktoerUrl;
        this.tokenProviderUrl = tokenProviderUrl;
        this.username = username;
        this.password = password;

        var converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));
        this.restTemplate.getMessageConverters().add(0, converter);
    }

    public Map<String, String> lookupAktoerIdFromFnr(
            List<String> fnrs,
            String environment
    ) {
        var uriTemplate = new UriTemplate(String.format(aktoerUrl, environment) + "/v1/identer?identgruppe=AktoerId&gjeldende=true");

        var headers = new HttpHeaders();
        headers.add("Nav-Consumer-Id", "Synt");
        headers.add("Nav-Call-Id", "Synt");
        headers.add("Nav-Personidenter", fnrs.toString().substring(1, fnrs.toString().length() - 1));
        var authToken = getAuthToken(environment, username, password);
        if ("".equals(authToken)) {
            log.error("Fikk ikke et token fra IDA, sjekk Z-bruker");
            return Collections.emptyMap();
        }
        headers.add("Authorization", authToken);

        var request = new RequestEntity<Map<String, String>>(headers, HttpMethod.GET, uriTemplate.expand());
        var response = restTemplate.exchange(request, RESPONSE_TYPE);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.warn("Kunne ikke hente aktør id for identer: {}", fnrs.toString().replaceAll("[\r\n]", ""));
            return Collections.emptyMap();
        }

        return mapResponseToAktoerFnr(response);
    }

    private Map<String, String> mapResponseToAktoerFnr(ResponseEntity<Map<String, AktoerResponse>> response) {
        Map<String, String> aktoerFnr = new HashMap<>();
        if (response.getBody() != null) {
            for (var entry : response.getBody().entrySet()) {
                var aktoerId = "";
                if (entry.getValue().getIdenter() == null) {
                    log.warn("{} Ident: {}", entry.getValue().getFeilmelding(), entry.getKey());
                    continue;
                }
                for (var aktoer : entry.getValue().getIdenter()) {
                    if (aktoer.isGjeldende() && "AktoerId".equals(aktoer.getIdentgruppe())) {
                        aktoerId = aktoer.getIdent();
                        break;
                    }
                }
                aktoerFnr.put(entry.getKey(), aktoerId);
            }
        }
        return aktoerFnr;
    }

    private String getAuthToken(
            String environment,
            String username,
            String password
    ) {
        var envShort = environment.substring(0, 1).toUpperCase();
        var uriTemplate = new UriTemplate(tokenProviderUrl + "/oidctoken_full?ident={user}&passord={pw}&stack={envShort}");
        var request = new RequestEntity<String>(HttpMethod.GET, uriTemplate.expand(username, password, envShort));

        var response = restTemplate.exchange(request, IdaResponse.class);

        IdaResponse idaResponse = response.getBody();
        if (idaResponse != null && response.getStatusCode() == HttpStatus.OK) {
            return idaResponse.getTokenType() + " " + idaResponse.getIdToken();
        } else {
            log.warn("Kunne ikke hente token fra Ida: {}", response.getStatusCode());
        }
        return "";
    }
}
