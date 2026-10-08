package no.nav.testnav.apps.statusfrontend.fagsystem.technical;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SecondBatchTechnicalStatusProperties;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

@UtilityClass
class TechnicalStatusRetry {

    static Retry transientFailures(SecondBatchTechnicalStatusProperties properties) {
        return retryOn(properties, TechnicalStatusRetry::isTransient);
    }

    static Retry retryOn(SecondBatchTechnicalStatusProperties properties, Predicate<Throwable> retryable) {
        return Retry.fixedDelay(properties.getRetryAttempts(), properties.getRetryDelay())
                .filter(retryable)
                .onRetryExhaustedThrow((_, retrySignal) -> retrySignal.failure());
    }

    static boolean isTransient(Throwable throwable) {
        if (throwable instanceof TimeoutException || throwable instanceof WebClientRequestException) {
            return true;
        }
        if (throwable instanceof WebClientResponseException responseException) {
            var statusCode = responseException.getStatusCode();
            return statusCode.is5xxServerError() || statusCode.value() == 408 || statusCode.value() == 429;
        }
        return false;
    }
}
