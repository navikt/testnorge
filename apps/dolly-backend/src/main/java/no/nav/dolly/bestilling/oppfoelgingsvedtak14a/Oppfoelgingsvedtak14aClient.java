package no.nav.dolly.bestilling.oppfoelgingsvedtak14a;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.dto.Oppfoelgingsvedtak14aRequestDTO;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.norg2.Norg2Consumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.oppfoelgingsvedtak14a.RsOppfoelgingsvedtak14aDTO;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.OPPFOELGINGSVEDTAK14A;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oppfoelgingsvedtak14aClient implements ClientRegister {

    private final Oppfoelgingsvedtak14aConsumer oppfoelgingsvedtak14aConsumer;
    private final TransactionHelperService transactionHelperService;
    private final MapperFacade mapperFacade;
    private final Norg2Consumer norg2Consumer;
    private final PersonServiceConsumer personServiceConsumer;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                                BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getOppfoelgingsvedtak14a())) {
            return Mono.empty();
        }

        return oppdaterStatus(progress, getInfoVenter(OPPFOELGINGSVEDTAK14A.getBeskrivelse()))
                .flatMap(_ -> oppfoelgingsvedtak14aConsumer.startOppfoelgingsperiode(dollyPerson.getIdent()))
                .flatMap(response -> !response.getStatus().is2xxSuccessful() ?
                        Mono.error(new DollyFunctionalException("Feil= Start oppfølgingsperiode feilet, %s %s"
                                .formatted(response.getStatus(), response.getReason()))) :
                        Mono.just(response))
                .flatMap(_ -> isBlank(bestilling.getOppfoelgingsvedtak14a().getOppfolgingsEnhet()) ?
                        getNorgEnhet(dollyPerson.getIdent()) : Mono.just(bestilling.getOppfoelgingsvedtak14a().getOppfolgingsEnhet()))
                .map(norgEnhet -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("ident", dollyPerson.getIdent());
                    context.setProperty("norgEnhet", norgEnhet);
                    return mapperFacade.map(bestilling.getOppfoelgingsvedtak14a(), Oppfoelgingsvedtak14aRequestDTO.class, context);
                })
                .flatMap(bistandsVedtak -> {
                    var oppdatertVedtak = mapperFacade.map(bistandsVedtak, RsOppfoelgingsvedtak14aDTO.class);
                    bestilling.setOppfoelgingsvedtak14a(oppdatertVedtak);
                    return transactionHelperService.persister(progress.getBestillingId(), bestilling)
                            .thenReturn(bistandsVedtak);
                })
                .flatMap(oppfoelgingsvedtak14aConsumer::opprettBistandVedtak)
                .flatMap(response -> oppdaterStatus(progress, response.getStatus().is2xxSuccessful() ?
                        "OK" : "Feil= Opprett bistandsvedtak feilet, %s %s"
                        .formatted(response.getStatus(), response.getReason())))
                .onErrorResume(throwable -> oppdaterStatus(progress, throwable.getMessage()));
    }

    private Mono<String> getNorgEnhet(String ident) {

        return personServiceConsumer.getPdlPersoner(List.of(ident))
                .filter(personBolk -> nonNull(personBolk.getData()) &&
                                      !personBolk.getData().getHentGeografiskTilknytningBolk().isEmpty())
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentGeografiskTilknytningBolk)
                .next()
                .map(List::getFirst)
                .filter(geografiskTilknytningBolk -> nonNull(geografiskTilknytningBolk.getGeografiskTilknytning()))
                .map(PdlPersonBolk.GeografiskTilknytningBolk::getGeografiskTilknytning)
                .map(Oppfoelgingsvedtak14aClient::getEnhet)
                .flatMap(norg2Consumer::getNorgEnhet)
                .map(response -> isNull(response.getHttpStatus()) ? response.getEnhetNr() : "0315")
                .switchIfEmpty(Mono.just("0315"));
    }

    private static String getEnhet(PdlPersonBolk.GeografiskTilknytning tilknytning) {

        if (isNotBlank(tilknytning.getGtKommune())) {
            return tilknytning.getGtKommune();

        } else if (isNotBlank(tilknytning.getGtBydel())) {
            return tilknytning.getGtBydel();

        } else {
            return "030102";
        }
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return
                transactionHelperService.persister(progress, BestillingProgress::getBistandsbehovStatus,
                        BestillingProgress::setBistandsbehovStatus, StringUtils.left(status, 100));
    }

    @Override
    public void release(List<String> identer) {

        oppfoelgingsvedtak14aConsumer.slettBistandsvedtak(identer)
                .collectList()
                .subscribe(_ -> log.info("Slettet bistandbehov for {} identer", identer.size()),
                        error -> log.error("Feil ved sletting av bistandbehov for {} identer {}", identer.size(), error.getMessage()));
    }
}