package no.nav.dolly.bestilling.bistandsbehov;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.bistandsbehov.dto.BistandVedtakRequestDTO;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.norg2.Norg2Consumer;
import no.nav.dolly.consumer.norg2.dto.Norg2EnhetResponse;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
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
import static no.nav.dolly.domain.resultset.SystemTyper.BISTANDSBEHOV;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.poi.util.StringUtil.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BistandsbehovClient implements ClientRegister {

    private final BistandsbehovConsumer bistandsbehovConsumer;
    private final TransactionHelperService transactionHelperService;
    private final MapperFacade mapperFacade;
    private final Norg2Consumer norg2Consumer;
    private final PersonServiceConsumer personServiceConsumer;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                                BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getBistandsbehov())) {
            return Mono.empty();
        }

        return oppdaterStatus(progress, getInfoVenter(BISTANDSBEHOV.getBeskrivelse()))
                .flatMap(_ -> bistandsbehovConsumer.startOppfoelgingsperiode(dollyPerson.getIdent()))
                .flatMap(response -> !response.getStatus().is2xxSuccessful() ?
                        Mono.error(new DollyFunctionalException("Feil= Start oppfølgingsperiode feilet, %s %s"
                                .formatted(response.getStatus(), response.getReason()))) :
                        Mono.just(response))
                .flatMap(_ -> isBlank(bestilling.getBistandsbehov().getOppfolgingsEnhet()) ?
                        getNorgEnhet(dollyPerson.getIdent()) : Mono.just(bestilling.getBistandsbehov().getOppfolgingsEnhet()))
                .map(norgEnhet -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("ident", dollyPerson.getIdent());
                    context.setProperty("norgEnhet", norgEnhet);
                    return mapperFacade.map(bestilling.getBistandsbehov(), BistandVedtakRequestDTO.class, context);
                })
                .flatMap(bistandsbehovConsumer::opprettBistandVedtak)
                .flatMap(response -> oppdaterStatus(progress, response.getStatus().is2xxSuccessful() ?
                        "OK" : "Feil= Opprett bistandsvedtak feilet, %s %s"
                        .formatted(response.getStatus(), response.getReason())))
                .onErrorResume(throwable -> oppdaterStatus(progress, throwable.getMessage()));
    }

    private Mono<String> getNorgEnhet(String ident) {

        return personServiceConsumer.getPdlPersoner(List.of(ident))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentGeografiskTilknytningBolk)
                .next()
                .map(List::getFirst)
                .map(PdlPersonBolk.GeografiskTilknytningBolk::getGeografiskTilknytning)
                .map(BistandsbehovClient::getEnhet)
                .flatMap(norg2Consumer::getNorgEnhet)
                .map(response -> {
                    if (nonNull(response.getHttpStatus())) {
                        response.setEnhetNr("0315");
                    }
                    return response;
                })
                .map(Norg2EnhetResponse::getEnhetNr);
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

        bistandsbehovConsumer.slettBistandsvedtak(identer)
                .collectList()
                .subscribe(_ -> log.info("Slettet bistandbehov for {} identer", identer.size()));
    }
}