package no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.KontoregisterResourceStatus;
import no.nav.testnav.libs.dto.kontoregister.v1.HentKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.KontoDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetKontoregisterAccountCommand implements Callable<Mono<KontoregisterResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final OppdaterKontoRequestDTO expectedAccount;
    private final Duration timeout;

    @Override
    public Mono<KontoregisterResourceStatus> call() {
        return webClient.post()
                .uri("/kontoregister/api/system/v1/hent-aktiv-konto")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(new HentKontoRequestDTO(expectedAccount.getKontohaver()))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND
                            || response.statusCode() == HttpStatus.NO_CONTENT) {
                        return response.releaseBody()
                                .thenReturn(KontoregisterResourceStatus.emptyStatus());
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(KontoDTO.class)
                                .map(this::toStatus)
                                .defaultIfEmpty(KontoregisterResourceStatus.emptyStatus());
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<KontoregisterResourceStatus>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private KontoregisterResourceStatus toStatus(KontoDTO account) {
        var expected = expectedAccount.getKontohaver().equals(account.getKontohaver())
                && expectedAccount.getKontonummer().equals(account.getKontonummer())
                && account.getGyldigTom() == null;
        return new KontoregisterResourceStatus(false, expected);
    }
}
