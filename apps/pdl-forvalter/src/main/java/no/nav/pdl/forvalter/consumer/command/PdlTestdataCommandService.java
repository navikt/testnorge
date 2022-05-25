package no.nav.pdl.forvalter.consumer.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.HistoriskIdent;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.pdl.forvalter.metrics.Timed;
import no.nav.pdl.forvalter.utils.WebClientFilter;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;

@Slf4j
@Service
public class PdlTestdataCommandService {

    static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    static final String TEMA = "Tema";

    private static final String PDL_AKTOER_ADMIN_PREFIX = "/pdl-npid";
    private static final String PDL_PERSON_AKTOER_URL = PDL_AKTOER_ADMIN_PREFIX + "/api/npid";
    private static final String IDENTHISTORIKK = "historiskePersonidenter";
    private static final String INFO_STATUS = "Finner ikke forespurt ident i pdl-api";

    private static String getMessage(Throwable error) {

        return error instanceof WebClientResponseException webClientResponseException &&
                StringUtils.isNotBlank(webClientResponseException.getResponseBodyAsString()) ?
                webClientResponseException.getResponseBodyAsString() :
                error.getMessage();
    }

    private static OrdreResponseDTO.HendelseDTO errorHandling(Throwable error, Integer id) {

        return OrdreResponseDTO.HendelseDTO.builder()
                .id(id)
                .status(PdlStatus.FEIL)
                .error(getMessage(error))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_opprettNpidPperson"})
    public Mono<OrdreResponseDTO.HendelseDTO> opprettNpidPerson(WebClient webClient, String npid, String token) {

        return webClient
                .post()
                .uri(PDL_PERSON_AKTOER_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(npid))
                .exchangeToMono(response ->
                        Mono.just(OrdreResponseDTO.HendelseDTO.builder()
                                .status(PdlStatus.OK)
                                .build()))
                .doOnError(WebServerException.class, error -> log.error(error.getMessage(), error))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error -> Mono.just(errorHandling(error, null)));
    }

    @Timed(name = "providers", tags = {"operation", "pdl_opprettPerson"})
    public Mono<OrdreResponseDTO.HendelseDTO> opprettPerson(WebClient webClient,
                                                            String url,
                                                            String ident,
                                                            HistoriskIdent historiskeIdenter,
                                                            String token) {

        return webClient
                .post()
                .uri(builder -> builder.path(url)
                        .queryParam("kilde", "Dolly")
                        .queryParam(IDENTHISTORIKK, historiskeIdenter.getIdenter())
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .retrieve()
                .bodyToMono(PdlBestillingResponse.class)
                .flatMap(response -> Mono.just(OrdreResponseDTO.HendelseDTO.builder()
                        .status(PdlStatus.OK)
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebServerException.class, error -> log.error(error.getMessage(), error))
                .onErrorResume(error -> Mono.just(errorHandling(error, null)));
    }

    @Timed(name = "providers", tags = {"operation", "pdl_bestillingPersonopplysning"})
    public Mono<OrdreResponseDTO.HendelseDTO> opprettPersonopplysning(WebClient webClient,
                                                                      String url,
                                                                      String ident,
                                                                      Object body,
                                                                      String token,
                                                                      Integer id) {

        return webClient
                .post()
                .uri(builder -> builder.path(url).build())
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .retrieve()
                .bodyToMono(PdlBestillingResponse.class)
                .flatMap(response -> Mono.just(OrdreResponseDTO.HendelseDTO.builder()
                        .id(id)
                        .status(PdlStatus.OK)
                        .hendelseId(response.getHendelseId())
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebServerException.class, error -> log.error(error.getMessage(), error))
                .onErrorResume(error -> Mono.just(errorHandling(error, id)));
    }

    @Timed(name = "providers", tags = {"operation", "pdl_slettAllePersonopplysninger"})
    public Mono<OrdreResponseDTO.HendelseDTO> deletePerson(WebClient webClient,
                                                           String url,
                                                           String ident,
                                                           String token) {

        return webClient
                .delete()
                .uri(builder -> builder.path(url).build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .retrieve()
                .bodyToMono(PdlBestillingResponse.class)
                .flatMap(response -> Mono.just(OrdreResponseDTO.HendelseDTO.builder()
                        .status(PdlStatus.OK)
                        .deletedOpplysninger(response.getDeletedOpplysninger())
                        .build()))
//                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
//                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebServerException.class, error -> log.error(error.getMessage(), error))
                .onErrorResume(error ->
                        Mono.just(OrdreResponseDTO.HendelseDTO.builder()
                                .status(getMessage(error).contains(INFO_STATUS) ? PdlStatus.OK : PdlStatus.FEIL)
                                .error(getMessage(error).contains(INFO_STATUS) ? null : getMessage(error))
                                .build())
                );
    }
}
