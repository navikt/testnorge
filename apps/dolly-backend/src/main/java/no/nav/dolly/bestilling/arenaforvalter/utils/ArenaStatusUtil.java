package no.nav.dolly.bestilling.arenaforvalter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arenaforvalter.ArenaUtils;
import no.nav.dolly.bestilling.arenaforvalter.dto.AapResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@UtilityClass
public class ArenaStatusUtil {

    public static final String MILJOE_FMT = "%s$%s %s";

    public static final String BRUKER = "BRUKER";
    public static final String AAP115 = "AAP115";
    public static final String AAP = "AAP";
    public static final String DAGPENGER = "DAGP";

    public static Mono<String> getDagpengerStatus(ArenaNyeDagpengerResponse response, ErrorStatusDecoder errorStatusDecoder) {

        return Flux.concat(Flux.just(response.getStatus())
                                .filter(status -> !status.is2xxSuccessful())
                                .map(status -> errorStatusDecoder.getErrorText(response.getStatus(), getMessage(response.getFeilmelding()))),
                        Flux.fromIterable(response.getNyeDagp())
                                .filter(nyDagP -> nonNull(nyDagP.getNyeDagpResponse()))
                                .map(nyDagP ->  "JA".equals(nyDagP.getNyeDagpResponse().getUtfall()) ?
                                        "OK" :
                                        encodeStatus(ArenaUtils.AVSLAG + nyDagP.getNyeDagpResponse().getBegrunnelse()))
                                .collect(Collectors.joining()),
                        Flux.fromIterable(response.getNyeDagpResponse())
                                .map(nyDagP -> "JA".equals(nyDagP.getUtfall()) ?
                                        "OK" :
                                        encodeStatus(ArenaUtils.AVSLAG + nyDagP.getBegrunnelse()))
                                .collect(Collectors.joining()),
                        Flux.fromIterable(response.getNyeDagpFeilList())
                                .map(dagpFeil ->
                                        encodeStatus(String.format(ArenaUtils.STATUS_FMT, dagpFeil.getNyDagpFeilstatus(), dagpFeil.getMelding())))
                                .collect(Collectors.joining()))

                .collect(Collectors.joining());
    }

    public static Mono<String> getAapStatus(AapResponse response, ErrorStatusDecoder errorStatusDecoder) {

        if (response.getStatus().is2xxSuccessful() && response.getNyeAap().isEmpty() && response.getNyeAapFeilList().isEmpty()) {

            return Mono.just("OK");
        } else {

            return Flux.concat(Flux.just(response.getStatus())
                                    .filter(status -> !status.is2xxSuccessful())
                                    .map(status -> errorStatusDecoder.getErrorText(response.getStatus(), getMessage(response.getFeilmelding()))),
                            Flux.fromIterable(response.getNyeAap())
                                    .map(nyAap -> "JA".equals(nyAap.getUtfall()) ?
                                            "OK" :
                                            encodeStatus(ArenaUtils.AVSLAG + nyAap.getBegrunnelse()))
                                    .collect(Collectors.joining()),
                            Flux.fromIterable(response.getNyeAapFeilList())
                                    .map(aapFeil ->
                                            encodeStatus(String.format(ArenaUtils.STATUS_FMT, aapFeil.getNyAapFeilstatus(), aapFeil.getMelding())))
                                    .collect(Collectors.joining()))

                    .collect(Collectors.joining());
        }
    }

    public static String fmtResponse(String miljoe, String system, String status) {

        return encodeStatus(String.format(MILJOE_FMT, miljoe, system, isNotBlank(status) ? status : "OK"));
    }

    public static String getMessage(String jsonFeilmelding) {

        try {
            var status = new ObjectMapper().readValue(jsonFeilmelding, Map.class);
            return status.containsKey("message") ? (String) status.get("message") : jsonFeilmelding;

        } catch (JsonProcessingException e) {
            log.warn("Feilet å dekode json status fra Arena: {}", jsonFeilmelding);
            return jsonFeilmelding;
        }
    }
}