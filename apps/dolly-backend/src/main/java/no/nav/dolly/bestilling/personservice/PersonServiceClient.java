package no.nav.dolly.bestilling.personservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.dto.PersonServiceResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@Order(5)
@RequiredArgsConstructor
public class PersonServiceClient implements ClientRegister {

    private static final int TIMEOUT = 100;
    private static final int MAX_SEKUNDER = 30;

    private final PersonServiceConsumer personServiceConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var startTime = System.currentTimeMillis();
        return getPersonService(LocalTime.now().plusSeconds(MAX_SEKUNDER),
                new PersonServiceResponse(), dollyPerson.getHovedperson())
                .doOnNext(status -> logStatus(status, startTime))
                .map(status -> futurePersist(progress, status));
        }

    private ClientFuture futurePersist(BestillingProgress progress, PersonServiceResponse status) {

        return () -> {
            progress.setPdlSync(status.getStatus().is2xxSuccessful() && isTrue(status.getExists()));
            progress.setFeil(isNotBlank(status.getFeilmelding()) ?
                    errorStatusDecoder.getErrorText(status.getStatus(), status.getFeilmelding()) : null);
            transactionHelperService.persister(progress);
            return progress;
        };
    }

    private static void logStatus(PersonServiceResponse status, long startTime) {

        if (status.getStatus().is2xxSuccessful() && isTrue(status.getExists())) {
            log.info("Synkronisering mot PersonService (isPerson) for {} tok {} ms.",
                    status.getIdent(), System.currentTimeMillis() - startTime);

        } else  if (status.getStatus().is2xxSuccessful() && isNotTrue(status.getExists())) {
            log.error("Synkronisering mot PersonService (isPerson) for {} gitt opp etter {} ms.",
                    status.getIdent(), System.currentTimeMillis() - startTime);
        } else {
            log.error("Feilet å sjekke om person finnes for ident {}, medgått tid {} ms.",
                    status.getIdent(), System.currentTimeMillis() - startTime);
        }
    }

    private Flux<PersonServiceResponse> getPersonService(LocalTime time, PersonServiceResponse response, String ident) {

        if (isTrue(response.getExists()) || LocalTime.now().isAfter(time) ||
                nonNull(response.getStatus()) && !response.getStatus().is2xxSuccessful()) {
            return Flux.just(response);

        } else {
            return Flux.just(1)
                    .delayElements(Duration.ofMillis(TIMEOUT))
                    .flatMap(delayed -> personServiceConsumer.isPerson(ident)
                            .flatMapMany(resultat -> getPersonService(LocalTime.now(), resultat, ident)));
        }
    }

    @Override
    public void release(List<String> identer) {
        // Ikke relevant
    }
}
