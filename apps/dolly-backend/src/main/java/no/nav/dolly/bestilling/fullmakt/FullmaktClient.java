package no.nav.dolly.bestilling.fullmakt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.fullmakt.dto.FullmaktResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class FullmaktClient implements ClientRegister {

    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;
    private final FullmaktConsumer fullmaktConsumer;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getFullmakt()) && !bestilling.getFullmakt().isEmpty()) {

            bestilling.setFullmakt(bestilling.getFullmakt().stream().map(fullmakt -> {
                if (isNull(fullmakt.getFullmektig())) {
                    fullmakt.setFullmektig(dollyPerson.getIdent());
                }
                return fullmakt;
            }).toList());

            return Flux.from(Flux.just(bestilling.getFullmakt()).flatMap(fullmakt -> fullmaktConsumer.createFullmaktData(fullmakt, dollyPerson.getIdent()))
                    .map(this::getStatus)
                    .map(status -> futurePersist(progress, status)));
        }

        //TODO: Kunne opprette fullmakt ved gjeldende person i gruppen eller lage ny gjennom pdl-forvalter

        return Flux.empty();
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(ident -> {
            var fullmaktResponse = fullmaktConsumer.getFullmaktData(List.of(ident)).blockFirst();
            fullmaktResponse.getFullmakt().forEach(fullmakt -> {
                fullmaktConsumer.deleteFullmaktData(ident, fullmakt.getFullmaktId()).block();
            });
        });
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setFullmaktStatus, status);
            return progress;
        };
    }

    private String getStatus(FullmaktResponse response) {

        return response.getStatus().is2xxSuccessful() ? "OK" :
                errorStatusDecoder.getErrorText(response.getStatus(), response.getMelding());
    }
}
