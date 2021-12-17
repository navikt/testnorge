package no.nav.testnav.apps.importfratpsfservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmelding;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmeldingGruppe;
import no.nav.testnav.apps.importfratpsfservice.exception.BadRequestException;
import no.nav.testnav.apps.importfratpsfservice.exception.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class TpsfGetSkdMeldingsgrupperCommand implements Callable<Flux<SkdEndringsmeldingGruppe>> {

    private static final String TPSF_SKD_GRUPPE_URL = "/api/v1/endringsmelding/skd/grupper";
    private static final String GRUPPER_FRA_TPSF = "Les grupper fra TPSF";

    private final WebClient webClient;
    private final String token;

    protected static String getMessage(Throwable error) {

        return error instanceof WebClientResponseException ?
                ((WebClientResponseException) error).getResponseBodyAsString() :
                error.getMessage();
    }

    @Override
    public Flux<SkdEndringsmeldingGruppe> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(TPSF_SKD_GRUPPE_URL).build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(SkdEndringsmeldingGruppe.class)
                .onErrorResume(throwable -> {
                    log.error(getMessage(throwable));
                    if (throwable instanceof WebClientResponseException) {
                        if (((WebClientResponseException) throwable).getStatusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new NotFoundException(GRUPPER_FRA_TPSF + getMessage(throwable)));
                        } else {
                            return Mono.error(new BadRequestException(GRUPPER_FRA_TPSF + getMessage(throwable)));
                        }
                    } else {
                        return Mono.error(new InternalError(GRUPPER_FRA_TPSF + getMessage(throwable)));
                    }
                });
    }
}
