package no.nav.dolly.bestilling.skattekort;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skattekort.domain.OpprettSkattekortRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                              BestillingProgress progress, boolean isOpprettEndre) {

        return Mono.just(bestilling)
                .filter(bestilling1 -> nonNull(bestilling1.getSkattekort()))
                .flatMapMany(bestilling1 -> Flux.fromIterable(bestilling1.getSkattekort().getArbeidsgiverSkatt()))
                .flatMap(arbeidsgiver -> Flux.fromIterable(arbeidsgiver.getArbeidstaker()))
                .doOnNext(skattekortmelding -> skattekortmelding.setArbeidstakeridentifikator(dollyPerson.getIdent()))
                .map(skattekortmelding -> mapperFacade.map(skattekortmelding, OpprettSkattekortRequest.class))
                .flatMap(skattekortConsumer::sendSkattekort)
                .collect(Collectors.joining(","))
                .flatMap(status -> oppdaterStatus(progress, status));
    }

    @Override
    public void release(List<String> identer) {

    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setSkattekortStatus, status);
    }
}
