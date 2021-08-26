package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus.FEIL;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@RequiredArgsConstructor
public class PdlOpprettArtifactCommandPdl extends PdlTestdataCommand {

    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final Object body;
    private final String token;
    private final Integer id;

    private static String buildErrorStatus(PdlBestillingResponse response) {

        return new StringBuilder()
                .append("message: ")
                .append(response.getMessage())
                .append(", details: ")
                .append(response.getDetails().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .toString();
    }

    @Override
    public Mono<OrdreResponseDTO.HendelseDTO> call() {

        return webClient
                .post()
                .uri(builder -> builder.path(url).build())
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.createException()
                                .flatMap(error -> Mono.just(errorHandling(error, id)));
                    }
                    return clientResponse.bodyToMono(PdlBestillingResponse.class)
                            .map(value -> OrdreResponseDTO.HendelseDTO.builder()
                                    .id(id)
                                    .status(isBlank(value.getMessage()) ? PdlStatus.OK : FEIL)
                                    .hendelseId(value.getHendelseId())
                                    .error(isNotBlank(value.getMessage()) ? buildErrorStatus(value) : null)
                                    .build());
                })
                .doOnError(error -> Mono.just(errorHandling(error, id)));
    }
}
