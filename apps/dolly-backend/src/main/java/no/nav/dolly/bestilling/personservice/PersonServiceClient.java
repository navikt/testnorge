package no.nav.dolly.bestilling.personservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
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

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@Order(5)
@RequiredArgsConstructor
public class PersonServiceClient {

    private static final String PDL_SYNC_START = "Synkronisering mot PDL startet ...";
    private static final int TIMEOUT = 100;
    private static final int MAX_SEKUNDER = 30;

    private final PersonServiceConsumer personServiceConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;

    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        progress.setPdlPersonStatus(PDL_SYNC_START);
        if (!dollyPerson.isOrdre()) {
            transactionHelperService.persister(progress);
        }
        var startTime = System.currentTimeMillis();
        return getPersonService(LocalTime.now().plusSeconds(MAX_SEKUNDER), LocalTime.now(),
                new PersonServiceResponse(), dollyPerson.getHovedperson())
                .flatMap(status -> logStatus(status, startTime)
                        .map(status2 -> futurePersist(dollyPerson, progress, status, status2)));
    }

    private ClientFuture futurePersist(DollyPerson dollyPerson, BestillingProgress progress, PersonServiceResponse status, String status2) {

        return () -> {
            progress.setPdlSync(status.getStatus().is2xxSuccessful() && isTrue(status.getExists()));
            progress.setPdlPersonStatus(status2);
            progress.setFeil(isNotBlank(status.getFeilmelding()) ?
                    errorStatusDecoder.getErrorText(status.getStatus(), status.getFeilmelding()) : null);
            if (!dollyPerson.isOrdre()) {
                transactionHelperService.persister(progress);
            }
            return progress;
        };
    }

    private Flux<String> logStatus(PersonServiceResponse status, long startTime) {

        if (status.getStatus().is2xxSuccessful() && isTrue(status.getExists())) {
            log.info("Synkronisering mot PersonService (isPerson) for {} tok {} ms.",
                    status.getIdent(), System.currentTimeMillis() - startTime);
            return Flux.just(String.format("Synkronisering mot PDL tok %d ms.",
                    System.currentTimeMillis() - startTime));

        } else if (status.getStatus().is2xxSuccessful() && isNotTrue(status.getExists())) {
            log.error("Synkronisering mot PersonService (isPerson) for {} gitt opp etter {} ms.",
                    status.getIdent(), System.currentTimeMillis() - startTime);
            return Flux.just(String.format("Feil: Synkronisering mot PDL gitt opp etter %d sekunder.",
                    MAX_SEKUNDER));

        } else {
            log.error("Feilet å sjekke om person finnes for ident {}, medgått tid {} ms, feil {}.",
                    status.getIdent(), System.currentTimeMillis() - startTime,
                    errorStatusDecoder.getErrorText(status.getStatus(),
                            status.getFeilmelding()));
            return Flux.just("Feilet å skjekke status, se logg!");
        }
    }

    private Flux<PersonServiceResponse> getPersonService(LocalTime tidSlutt, LocalTime tidNo, PersonServiceResponse response, String ident) {

        if (isTrue(response.getExists()) || tidNo.isAfter(tidSlutt) ||
                nonNull(response.getStatus()) && !response.getStatus().is2xxSuccessful()) {
            return Flux.just(response);

        } else {
            return Flux.just(1)
                    .delayElements(Duration.ofMillis(TIMEOUT))
                    .flatMap(delayed -> personServiceConsumer.isPerson(ident)
                            .flatMapMany(resultat -> getPersonService(tidSlutt, LocalTime.now(), resultat, ident)));
        }
    }
}
