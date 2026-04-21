package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RequiredArgsConstructor
public class VegadresseServiceCommand implements Callable<Mono<VegadresseDTO[]>> {

    private static final String ADRESSER_VEG_URL = "/api/v1/adresser/vegadresse";

    private final WebClient webClient;
    private final no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO query;
    private final String matrikkelId;
    private final String token;

    @Override
    public Mono<VegadresseDTO[]> call() {
        return webClient
                .get()
                .uri(builder -> builder.path(ADRESSER_VEG_URL).queryParams(getQuery()).build())
                .header("antall", "1")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(VegadresseDTO[].class)
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound ||
                                throwable instanceof WebClientResponseException.BadRequest ||
                                Exceptions.isRetryExhausted(throwable),
                        throwable -> Mono.just(new VegadresseDTO[]{defaultAdresse()}));
    }

    public static VegadresseDTO defaultAdresse() {

        return VegadresseDTO.builder()
                .matrikkelId("285693617")
                .adressenavn("FYRSTIKKALLÉEN")
                .postnummer("0661")
                .husnummer(2)
                .kommunenummer("0301")
                .poststed("Oslo")
                .build();
    }

    private MultiValueMap<String, String> getQuery() {
        return new LinkedMultiValueMap<>(
                new LinkedHashMap<>(Map.of(
                                "matrikkelId", filterArtifact(matrikkelId),
                                "adressenavn", filterArtifact(query.getAdressenavn()),
                                "husnummer", filterArtifact(query.getHusnummer()),
                                "husbokstav", filterArtifact(query.getHusbokstav()),
                                "postnummer", filterArtifact(query.getPostnummer()),
                                "kommunenummer", filterArtifact(query.getKommunenummer()),
                                "bydelsnummer", filterArtifact(query.getBydelsnummer()),
                                "tilleggsnavn", filterArtifact(query.getTilleggsnavn()))
                        .entrySet().stream()
                        .filter(entry -> isNotBlank(entry.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> List.of(entry.getValue())))));
    }

    private static String filterArtifact(String artifact) {
        return isNotBlank(artifact) ? artifact : "";
    }
}
