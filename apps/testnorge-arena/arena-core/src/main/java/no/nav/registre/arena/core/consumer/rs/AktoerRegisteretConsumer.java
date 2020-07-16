package no.nav.registre.arena.core.consumer.rs;

import static no.nav.registre.arena.core.consumer.rs.util.Headers.AUTHORIZATION;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CONSUMER_ID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.arena.core.consumer.rs.response.AktoerResponse;
import no.nav.registre.arena.core.security.TokenService;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;

@Slf4j
@Component
@DependencyOn(value = "aktoerregister", external = true)
public class AktoerRegisteretConsumer {

    private static final ParameterizedTypeReference<Map<String, AktoerResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;
    private final TokenService tokenService;

    private final String aktoerUrl;

    public AktoerRegisteretConsumer(
            RestTemplate restTemplate,
            TokenService tokenService,
            @Value("${aktoerregister.api.url}") String aktoerUrl
    ) {
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
        this.aktoerUrl = aktoerUrl;

        var converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));
        this.restTemplate.getMessageConverters().add(0, converter);
    }

    public Map<String, String> hentAktoerIderTilIdenter(
            List<String> identer,
            String miljoe
    ) {
        var uriTemplate = new UriTemplate(String.format(aktoerUrl, miljoe) + "/v1/identer?identgruppe=AktoerId&gjeldende=true");

        var headers = new HttpHeaders();
        headers.add(CALL_ID, NAV_CALL_ID);
        headers.add(CONSUMER_ID, NAV_CONSUMER_ID);
        headers.add("Nav-Personidenter", identer.toString().substring(1, identer.toString().length() - 1));
        headers.add(AUTHORIZATION, tokenService.getIdToken());

        var request = new RequestEntity<Map<String, AktoerResponse>>(headers, HttpMethod.GET, uriTemplate.expand());
        var response = restTemplate.exchange(request, RESPONSE_TYPE);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.warn("Kunne ikke hente aktør id for identer: {}", identer.toString().replaceAll("[\r\n]", ""));
            return Collections.emptyMap();
        }

        Map<String, String> aktoerFnr = new HashMap<>();
        if (response.getBody() != null) {
            for (var entry : response.getBody().entrySet()) {
                var aktoerId = "";
                if (entry.getValue().getIdenter() == null) {
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
}
