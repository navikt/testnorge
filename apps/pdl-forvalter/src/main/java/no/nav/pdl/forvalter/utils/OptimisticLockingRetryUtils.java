package no.nav.pdl.forvalter.utils;

import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Flere artefakt-tjenester (Sivilstand, ForelderBarnRelasjon, Foreldreansvar) gjør uavhengige
 * "les relatert person -> muter -> lagre"-operasjoner på personer utenfor den ytre transaksjonen
 * som allerede blir oppdatert i den samme forespørselen. Dersom samme person-rad blir lest og
 * lagret flere ganger på denne måten (f.eks. både som følge av sivilstand og forelder-barn-relasjon
 * i én og samme bestilling), kan et forsøk bruke et utdatert versjonsnummer og feile med
 * {@link OptimisticLockingFailureException}. Denne hjelpemetoden lar hele
 * "les -> muter -> lagre"-kjeden kjøres på nytt med et friskt lest utgangspunkt dersom det skjer.
 */
public final class OptimisticLockingRetryUtils {

    private OptimisticLockingRetryUtils() {
    }

    public static <T> Mono<T> retryOnOptimisticLockingFailure(Mono<T> mono) {

        return mono.retryWhen(Retry.backoff(3, Duration.ofMillis(50))
                .filter(OptimisticLockingFailureException.class::isInstance));
    }
}
