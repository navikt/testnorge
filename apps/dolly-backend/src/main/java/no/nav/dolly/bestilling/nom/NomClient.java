package no.nav.dolly.bestilling.nom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.nom.domain.NomRessursRequest;
import no.nav.dolly.bestilling.nom.domain.NomRessursResponse;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.RsNomData;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.NOM;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.util.DateZoneUtil.CET;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class NomClient implements ClientRegister {

    private final ApplicationConfig applicationConfig;
    private final NomConsumer nomConsumer;
    private final PersonServiceConsumer personServiceConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getNomdata())) {
            return Mono.empty();
        }

        return oppdaterStatus(progress, getInfoVenter(NOM.name()))
                .then(nomConsumer.hentRessurs(dollyPerson.getIdent()))
                .flatMap(ressurs -> {
                    if (ressurs.getStatus().equals(HttpStatus.NOT_FOUND)) {
                        return mapTilNomRequest(bestilling.getNomdata(), dollyPerson)
                                .flatMap(nomConsumer::opprettRessurs);
                    } else if (isNull(ressurs.getSluttDato()) && nonNull(bestilling.getNomdata().getSluttDato())) {
                        return nomConsumer.avsluttRessurs(dollyPerson.getIdent(),
                                bestilling.getNomdata().getSluttDato().toLocalDate());
                    } else if (nonNull(ressurs.getSluttDato()) && ressurs.getSluttDato().isBefore(LocalDate.now(CET))) {
                        return mapTilNomRequest(bestilling.getNomdata(), dollyPerson)
                                .flatMap(nomConsumer::opprettRessurs);
                    } else {
                        return Mono.just(ressurs);
                    }
                })
                .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                .onErrorResume(error -> Mono.just(NomRessursResponse.builder()
                                .melding(WebClientError.describe(error).getMessage())
                                .build()))
                .map(NomClient::getStatus)
                .flatMap(status -> oppdaterStatus(progress, status));
    }

    private Mono<NomRessursRequest> mapTilNomRequest(RsNomData nomdata, DollyPerson dollyPerson) {

        return personServiceConsumer.getPdlPersoner(List.of(dollyPerson.getIdent()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .map(PdlPersonBolk.PersonBolk::getPerson)
                .map(PdlPerson.Person::getNavn)
                .flatMap(Flux::fromIterable)
                .filter(navn -> nonNull(navn.getMetadata()) && isFalse(navn.getMetadata().getHistorisk()))
                .next()
                .map(navn -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("ident", dollyPerson.getIdent());
                    context.setProperty("navn", navn);
                    return mapperFacade.map(nomdata, NomRessursRequest.class, context);
                });
    }

    @Override
    public void release(List<String> identer) {

        Flux.fromIterable(identer)
                .flatMap(ident -> nomConsumer.avsluttRessurs(ident, LocalDate.now(CET)))
                .collectList()
                .subscribe(result ->
                        log.info("Nom, avsluttet {} identer", identer.size()));
    }

    private static String getStatus(NomRessursResponse response) {

        return isNotBlank(response.getMelding()) ? "FEIL= " + response.getMelding() : "OK";
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setNomStatus, status);
    }
}