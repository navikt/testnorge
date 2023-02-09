package no.nav.dolly.bestilling.udistub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper.Status;
import no.nav.dolly.bestilling.udistub.util.UdiMergeService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiStubClient implements ClientRegister {

    private final ErrorStatusDecoder errorStatusDecoder;
    private final UdiMergeService udiMergeService;
    private final UdiStubConsumer udiStubConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getUdistub())) {

            return Flux.from(getPersonData(List.of(dollyPerson.getIdent()))
                            .flatMap(persondata -> udiStubConsumer.getUdiPerson(dollyPerson.getIdent())
                                    .map(eksisterende -> udiMergeService.merge(bestilling.getUdistub(),
                                            eksisterende, persondata))
                                    .flatMap(this::sendUdiPerson)
                                    .map(this::getStatus))
                            .collect(Collectors.joining()))
                    .map(status -> futurePersist(progress, status));
        }
        return Flux.empty();
    }

    @Override
    public void release(List<String> identer) {

        udiStubConsumer.deleteUdiPerson(identer)
                .subscribe(response -> log.info("Slettet identer fra Udistub"));
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(List<String> identer) {

        return pdlPersonConsumer.getPdlPersoner(identer)
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()));
    }

    private Mono<UdiPersonResponse> sendUdiPerson(UdiPersonWrapper wrapper) {

        return Status.NEW == wrapper.getStatus() ?
                udiStubConsumer.createUdiPerson(wrapper.getUdiPerson()) :
                udiStubConsumer.updateUdiPerson(wrapper.getUdiPerson());
    }

    private String getStatus(UdiPersonResponse response) {

        log.info("UdiStub {} UdiPerson {} ", response.getType() == UdiPersonResponse.InnsendingType.NEW ?
                "Opprettet" : "Oppdatert", response);
        return response.getStatus().is2xxSuccessful() ? "OK" :
                errorStatusDecoder.getErrorText(response.getStatus(), response.getReason());
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setUdistubStatus, status);
            return progress;
        };
    }
}
