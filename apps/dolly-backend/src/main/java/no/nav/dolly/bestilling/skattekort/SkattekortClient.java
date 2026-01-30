package no.nav.dolly.bestilling.skattekort;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class SkattekortClient implements ClientRegister {

    private final SkattekortConsumer skattekortConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                              BestillingProgress progress, boolean isOpprettEndre) {

        if (!nonNull(bestilling.getSkattekort())) {
            return Mono.just(progress);
        }

        SkattekortRequestDTO mapped = mapperFacade.map(bestilling.getSkattekort(), SkattekortRequestDTO.class);

        Flux<String> statusFlux = Flux.fromIterable(mapped.getArbeidsgiver())
                .map(arbeidsgiver -> SkattekortRequestDTO.builder()
                        .arbeidsgiver(List.of(arbeidsgiver))
                        .build())
                .flatMap(request -> skattekortConsumer.sendSkattekort(request))
                .map(response -> formatStatus(response));

        return statusFlux
                .collectList()
                .map(statuses -> String.join(",", statuses))
                .flatMap(status -> oppdaterStatus(progress, status));
    }

    @Override
    public void release(List<String> identer) {
        // Deletion is not yet supported
    }

    private String formatStatus(SkattekortResponse response) {
        if (response.isOK()) {
            return "OK";
        }
        return "FEIL: " + response.getFeilmelding();
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setSkattekortStatus, status);

    }
}
