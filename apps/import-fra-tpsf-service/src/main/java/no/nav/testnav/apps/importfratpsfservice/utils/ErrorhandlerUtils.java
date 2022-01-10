package no.nav.testnav.apps.importfratpsfservice.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.importfratpsfservice.exception.BadRequestException;
import no.nav.testnav.apps.importfratpsfservice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@UtilityClass
public class ErrorhandlerUtils {

    private static String getMessage(Throwable error) {

        return error instanceof WebClientResponseException ?
                ((WebClientResponseException) error).getResponseBodyAsString() :
                error.getMessage();
    }

    public static Mono handleError(Throwable throwable, String legend) {
        log.error(getMessage(throwable));
        if (throwable instanceof WebClientResponseException) {
            if (((WebClientResponseException) throwable).getStatusCode() == HttpStatus.NOT_FOUND) {
                return Mono.error(new NotFoundException(legend + getMessage(throwable)));
            } else {
                return Mono.error(new BadRequestException(legend + getMessage(throwable)));
            }
        } else {
            return Mono.error(new InternalError(legend + getMessage(throwable)));
        }
    }
}
