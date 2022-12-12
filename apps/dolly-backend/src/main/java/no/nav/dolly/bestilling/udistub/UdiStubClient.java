package no.nav.dolly.bestilling.udistub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonWrapper.Status;
import no.nav.dolly.bestilling.udistub.util.UdiMergeService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselSlutt;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVenterTekst;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class UdiStubClient implements ClientRegister {

    private final ErrorStatusDecoder errorStatusDecoder;
    private final UdiMergeService udiMergeService;
    private final UdiStubConsumer udiStubConsumer;
    private final PersonServiceConsumer personServiceConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getUdistub())) {

            progress.setUdistubStatus(encodeStatus(getInfoVenter("UdiStub")));
            transactionHelperService.persister(progress);

            personServiceConsumer.getPdlSyncReady(dollyPerson.getHovedperson())
                    .flatMap(isReady -> (isReady ?
                            getPersonData(List.of(dollyPerson.getHovedperson()))
                                    .flatMap(persondata -> udiStubConsumer.getUdiPerson(dollyPerson.getHovedperson())
                                            .map(eksisterende -> udiMergeService.merge(bestilling.getUdistub(),
                                                    eksisterende, persondata))
                                            .flatMap(this::sendUdiPerson)
                                            .map(this::getStatus))
                                    .collect(Collectors.joining()) :

                            Mono.just(encodeStatus(getVarselSlutt("UdiStub")))
                    ))
                    .subscribe(resultat -> {
                        progress.setUdistubStatus(resultat);
                        transactionHelperService.persister(progress);
                    });
        }
        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

        udiStubConsumer.deleteUdiPerson(identer)
                .subscribe(response -> log.info("Slettet identer fra Udistub"));
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getUdistub()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getUdistubStatus()) &&
                                !entry.getUdistubStatus().contains(getVenterTekst()));
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

        return response.getStatus().is2xxSuccessful() ? "OK" :
                errorStatusDecoder.getErrorText(response.getStatus(), response.getReason());
    }
}
