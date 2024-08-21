package no.nav.dolly.bestilling.skattekort;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class SkattekortClient implements ClientRegister {

    private final SkattekortConsumer skattekortConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                          BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.just(bestilling)
                .filter(bestilling1 -> nonNull(bestilling1.getSkattekort()))
                .map(bestilling1 -> mapperFacade.map(bestilling1.getSkattekort(), SkattekortRequestDTO.class))
                .doOnNext(skattekort ->
                        skattekort.getArbeidsgiver()
                                .forEach(arbeidsgiver -> arbeidsgiver.getArbeidstaker()
                                        .forEach(arbeidstaker -> arbeidstaker.setArbeidstakeridentifikator(dollyPerson.getIdent()))))
                .flatMap(skattkort -> Flux.fromIterable(skattkort.getArbeidsgiver())
                        .map(arbeidsgiver -> SkattekortRequestDTO.builder()
                                .arbeidsgiver(List.of(arbeidsgiver))
                                .build())
                        .flatMap(skattekortConsumer::sendSkattekort)
                        .collect(Collectors.joining(",")))
                .map(status -> futurePersist(progress, status));
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setSkattekortStatus, status);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {
        // Deletion is not yet supported
    }
}
