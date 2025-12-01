package no.nav.dolly.bestilling.fullmakt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.fullmakt.dto.FullmaktPostResponse;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.FULLMAKT;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
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
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (bestilling.getFullmakt().isEmpty()) {
            return Mono.empty();
        }

        fiksDatoerForGjenoppretting(bestilling.getFullmakt(), isOpprettEndre);

        return oppdaterStatus(progress, getInfoVenter(FULLMAKT.name()))
                .then(Flux.fromIterable(bestilling.getFullmakt())
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
                        .collectList()
                        .map(this::getStatus)
                    .flatMap(status -> oppdaterStatus(progress, status)));
    }

    private static void fiksDatoerForGjenoppretting(List<RsFullmakt> fullmakter, Boolean isOpprettEndre) {

        if (isFalse(isOpprettEndre)) {
            fullmakter.forEach(fullmakt -> {
                if (nonNull(fullmakt.getGyldigFraOgMed()) && fullmakt.getGyldigFraOgMed().isBefore(now())) {
                    fullmakt.setGyldigFraOgMed(now());
                }
                if (nonNull(fullmakt.getGyldigTilOgMed())
                        && fullmakt.getGyldigTilOgMed().isBefore(fullmakt.getGyldigFraOgMed())) {
                    fullmakt.setGyldigTilOgMed(fullmakt.getGyldigFraOgMed().plusYears(1));
                }
            });
        }
    }

    @Override
    public void release(List<String> identer) {

        Flux.fromIterable(identer)
                .flatMap(ident -> fullmaktConsumer.getFullmaktData(ident)
                        .doOnNext(response -> log.info("Fullmakt response for {}: {}", ident, response))
                        .map(FullmaktPostResponse.Fullmakt::getFullmaktId)
                        .flatMap(fullmaktsId -> fullmaktConsumer.deleteFullmaktData(ident, fullmaktsId)))
                .collectList()
                .subscribe(result -> log.info("Fullmakt, slettet {} identer", identer.size()));
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setFullmaktStatus, status);
    }

    private String getStatus(List<FullmaktPostResponse> response) {

        return response.stream()
                .filter(status1 -> nonNull(status1.getStatus()) &&
                        !status1.getMelding().contains("finnes fra før"))
                .findFirst()
                .map(error -> errorStatusDecoder.getErrorText(error.getStatus(), error.getMelding()))
                .orElse("OK");
    }

    private String getFullName(PersonDTO person) {
        var navn = person.getNavn().getFirst();
        return (isBlank(navn.getMellomnavn()))
                ? "%s %s".formatted(navn.getFornavn(), navn.getEtternavn())
                : "%s %s %s".formatted(navn.getFornavn(), navn.getMellomnavn(), navn.getEtternavn());
    }
}