package no.nav.dolly.bestilling.fullmakt;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.fullmakt.dto.FullmaktResponse;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.http.util.TextUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class FullmaktClient implements ClientRegister {

    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;
    private final FullmaktConsumer fullmaktConsumer;
    private final PdlDataConsumer pdlDataConsumer;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getFullmakt()) && !bestilling.getFullmakt().isEmpty()) {

            bestilling.setFullmakt(bestilling.getFullmakt().stream().map(fullmakt -> {

                if (isBlank(fullmakt.getFullmektig())) {

                    fullmakt.setFullmektig(pdlDataConsumer.getPersoner(Collections.singletonList(dollyPerson.getIdent()))
                            .map(person -> person.getRelasjoner().stream()
                                    .peek(relasjon -> log.info("Relasjon for ident: {} - {}", dollyPerson.getIdent(), Json.pretty(relasjon)))
                                    .filter(relasjon -> relasjon.getRelasjonType().equals(RelasjonType.FULLMEKTIG))
                                    .findFirst()
                                    .map(relasjon -> relasjon.getRelatertPerson().getIdent())
                                    .orElseThrow(() -> new DollyFunctionalException("Fant ikke fullmektig som relasjon for person " + dollyPerson.getIdent())))
                            .blockFirst());
                }
                return fullmakt;
            }).toList());

            return Flux.from(Flux.just(bestilling.getFullmakt())
                    .flatMap(fullmakt -> fullmaktConsumer.createFullmaktData(fullmakt, dollyPerson.getIdent()))
                    .map(this::getStatus)
                    .map(status -> futurePersist(progress, status)));
        }

        return Flux.empty();
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(ident -> {
            var fullmaktResponse = fullmaktConsumer.getFullmaktData(List.of(ident)).blockFirst();
            fullmaktResponse.getFullmakt().forEach(fullmakt -> fullmaktConsumer.deleteFullmaktData(ident, fullmakt.getFullmaktId()).block());
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
