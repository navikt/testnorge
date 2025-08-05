package no.nav.dolly.bestilling.fullmakt;

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
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.resultset.SystemTyper.FULLMAKT;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
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

        if (!bestilling.getFullmakt().isEmpty()) {

            transactionHelperService.persister(progress, BestillingProgress::setFullmaktStatus,
                    getInfoVenter(FULLMAKT.name()));

            return Flux.fromIterable(bestilling.getFullmakt())
                    .flatMap(fullmakt -> {
                        fullmakt.setFullmaktsgiver(dollyPerson.getIdent());
                        if (isBlank(fullmakt.getFullmektig())) {
                            return pdlDataConsumer.getPersoner(List.of(dollyPerson.getIdent()))
                                    .flatMap(person -> Flux.fromIterable(person.getRelasjoner())
                                            .filter(relasjon -> relasjon.getRelasjonType().equals(RelasjonType.FULLMEKTIG)))
                                    .next()
                                    .map(relasjon -> {
                                        fullmakt.setFullmektigsNavn(getFullName(relasjon.getRelatertPerson()));
                                        fullmakt.setFullmektig(relasjon.getRelatertPerson().getIdent());
                                        return fullmakt;
                                    });
                        } else {
                            return Mono.just(fullmakt);
                        }
                    })
                    .collectList()
                    .flatMapMany(fullmakter -> fullmaktConsumer.createFullmaktData(fullmakter, dollyPerson.getIdent()))
                    .map(this::getStatus)
                    .map(status -> futurePersist(progress, status));
        }

        return Flux.empty();
    }

    @Override
    public void release(List<String> identer) {

        Flux.fromIterable(identer)
                .flatMap(ident -> fullmaktConsumer.getFullmaktData(List.of(ident))
                        .doOnNext(response -> log.info("Fullmakt response for {}: {}", ident, response))
                        .map(FullmaktResponse::getFullmakt)
                        .flatMap(Flux::fromIterable)
                        .map(FullmaktResponse.Fullmakt::getFullmaktId)
                        .flatMap(fullmaktsId -> fullmaktConsumer.deleteFullmaktData(ident, fullmaktsId)))
                .collectList()
                .subscribe(result -> log.info("Fullmakt, slettet {} identer", identer.size()));
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setFullmaktStatus, status);
            return progress;
        };
    }

    private String getStatus(FullmaktResponse response) {

        return isNull(response.getStatus()) ? "OK" :
                errorStatusDecoder.getErrorText(response.getStatus(), response.getMelding());
    }

    private String getFullName(PersonDTO person) {
        var navn = person.getNavn().getFirst();
        return (isBlank(navn.getMellomnavn()))
                ? "%s %s".formatted(navn.getFornavn(), navn.getEtternavn())
                : "%s %s %s".formatted(navn.getFornavn(), navn.getMellomnavn(), navn.getEtternavn());
    }
}