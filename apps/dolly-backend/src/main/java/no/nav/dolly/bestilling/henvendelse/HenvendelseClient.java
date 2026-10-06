package no.nav.dolly.bestilling.henvendelse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseMeldingRequest;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseResponse;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseSamtalereferatRequest;
import no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.norg2.Norg2Consumer;
import no.nav.dolly.consumer.norg2.dto.Norg2EnhetResponse;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.truncate;

@Slf4j
@Service
@RequiredArgsConstructor
public class HenvendelseClient implements ClientRegister {

    private static final String AKTORID = "AKTORID";
    private static final int MAX_STATUS_LEN = 100;

    private final HenvendelseConsumer henvendelseConsumer;
    private final MapperFacade mapperFacade;
    private final Norg2Consumer norg2Consumer;
    private final PersonServiceConsumer personServiceConsumer;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getHenvendelse()) ||
            bestilling.getHenvendelse().getMeldinger().isEmpty() &&
            bestilling.getHenvendelse().getSamtalereferater().isEmpty()) {

            return Mono.empty();
        }

        return personServiceConsumer.getPdlPersoner(List.of(dollyPerson.getIdent()))
                .next()
                .flatMap(personbolk ->
                        Mono.zip(getAktorId(personbolk)
                                        .next(),
                                getNorgEnhet(personbolk)))
                .flatMapMany(tuple -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("aktorId", tuple.getT1());
                    context.setProperty("enhet", tuple.getT2());
                    return Flux.merge(
                            Flux.fromIterable(bestilling.getHenvendelse().getMeldinger())
                                    .map(melding -> mapperFacade.map(melding, HenvendelseMeldingRequest.class, context)),
                            Flux.fromIterable(bestilling.getHenvendelse().getSamtalereferater())
                                    .map(referat -> mapperFacade.map(referat, HenvendelseSamtalereferatRequest.class, context)));
                })
                .flatMap(henvendelseConsumer::sendHenvendelse)
                .map(status -> status.getStatus().is2xxSuccessful() ? "OK" : "Feil= %s:%s".formatted(
                        status.getType(), ErrorStatusDecoder.encodeStatus(status.getMelding())))
                .collect(Collectors.joining(","))
                .flatMap(status -> oppdaterStatus(progress, status));
    }

    @Override
    public void release(List<String> identer) {

        personServiceConsumer.getPdlPersoner(identer)
                .flatMap(HenvendelseClient::getAktorId)
                .flatMap(henvendelseConsumer::getHenvendelse)
                .map(HenvendelseResponse::getData)
                .flatMap(Flux::fromIterable)
                .map(HenvendelseResponse.Info::getKjedeId)
                .flatMap(henvendelseConsumer::deleteHenvendelse)
                .subscribe(_ -> log.info("Lukket henvendelser i Salesforce"));
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setHenvendelseStatus,
                truncate(status, MAX_STATUS_LEN));
    }

    private static @NonNull Flux<String> getAktorId(PdlPersonBolk personbolk) {

        return Flux.fromIterable(personbolk.getData().getHentIdenterBolk())
                .map(PdlPersonBolk.IdenterBolk::getIdenter)
                .flatMap(Flux::fromIterable)
                .filter(ident -> AKTORID.equals(ident.getGruppe()))
                .filter(ident -> isFalse(ident.getHistorisk()))
                .map(PdlPersonBolk.Identinformasjon::getIdent);
    }

    private @NonNull Mono<String> getNorgEnhet(PdlPersonBolk personbolk) {

        return Flux.fromIterable(personbolk.getData().getHentGeografiskTilknytningBolk())
                .filter(tilknytning -> nonNull(tilknytning.getGeografiskTilknytning()))
                .map(PdlPersonBolk.GeografiskTilknytningBolk::getGeografiskTilknytning)
                .map(PensjonforvalterUtils::getGeografiskTilknytning)
                .flatMap(norg2Consumer::getNorgEnhet)
                .filter(norgenhet -> nonNull(norgenhet.getEnhetNr()))
                .map(Norg2EnhetResponse::getEnhetNr)
                .collectList()
                .doOnNext(norgdata -> log.info("Mottatt norgdata: {}", norgdata))
                .map(norgdata -> !norgdata.isEmpty() ? norgdata.getFirst() : "0315");
    }
}
