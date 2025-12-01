package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO;
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

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RequiredArgsConstructor
public class MatrikkeladresseServiceCommand implements Callable<Mono<MatrikkeladresseDTO[]>> {

    private static final String ADRESSER_VEG_URL = "/api/v1/adresser/matrikkeladresse";

    private final WebClient webClient;
    private final no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO query;
    private final String matrikkelId;
    private final String token;

    public static MatrikkeladresseDTO defaultAdresse() {

        return MatrikkeladresseDTO.builder()
                .matrikkelId("24867173")
                .kommunenummer("4626")
                .gaardsnummer("41")
                .bruksnummer("89")
                .postnummer("5355")
                .poststed("KNARREVIK")
                .tilleggsnavn("VALEN")
                .build();
    }

    private static String filterArtifact(String artifact) {
        return isNotBlank(artifact) ? artifact : "";
    }

    private String nullcheck(Integer value) {
        return nonNull(value) ? value.toString() : null;
    }

    @Override
    public Mono<MatrikkeladresseDTO[]> call() {
        return webClient
                .get()
                .uri(builder -> builder.path(ADRESSER_VEG_URL).queryParams(getQuery()).build())
                .header("antall", "1")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(MatrikkeladresseDTO[].class)
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound ||
                                Exceptions.isRetryExhausted(throwable),
                        throwable -> Mono.just(new MatrikkeladresseDTO[]{defaultAdresse()}));
    }

    private MultiValueMap<String, String> getQuery() {
        return new LinkedMultiValueMap<>(
                new LinkedHashMap<>(
                        Map.of("matrikkelId", filterArtifact(matrikkelId),
                                        "kommunenummer", filterArtifact(query.getKommunenummer()),
                                        "gaardsnummer", filterArtifact(nullcheck(query.getGaardsnummer())),
                                        "bruksnummer", filterArtifact(nullcheck(query.getBruksnummer())),
                                        "postnummer", filterArtifact(query.getPostnummer()),
                                        "tilleggsnavn", filterArtifact(query.getTilleggsnavn()))
                                .entrySet().stream()
                                .filter(entry -> isNotBlank(entry.getValue()))
                                .collect(Collectors.toMap(Map.Entry::getKey, entry -> List.of(entry.getValue())))));
    }

}