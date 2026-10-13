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
import static no.nav.dolly.domain.resultset.SystemTyper.HENVENDELSE;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
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

        } else if (isOpprettEndre && !dollyPerson.isLeggTil()) {
            return oppdaterStatus(progress, ErrorStatusDecoder.encodeStatus("Feil: Henvendelse er kun støttet ved legg-til/endre"));

        } else if (!dollyPerson.isSalesForce()) {
            return oppdaterStatus(progress, ErrorStatusDecoder.encodeStatus("Feil: Henvendelse ikke sendt; gruppe mangler tag SALESFORCE"));
        }

        return oppdaterStatus(progress, getInfoVenter(HENVENDELSE.getBeskrivelse()))
                .then(personServiceConsumer.getPdlPersoner(List.of(dollyPerson.getIdent()))
                        .next())
                .filter(personbolk -> nonNull(personbolk.getData()))
                .flatMap(personbolk ->
                        Mono.zip(getAktorId(personbolk)
                                        .next(),
                                getNorgEnhet(personbolk)))
                .flatMap(tuple -> Mono.zip(
                        Mono.just(tuple.getT1()),
                        Mono.just(tuple.getT2()),
                        henvendelseConsumer.getHenvendelse(tuple.getT1())))
                .flatMapMany(tuple -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("aktorId", tuple.getT1());
                    context.setProperty("enhet", tuple.getT2());
                    return Flux.merge(
                            Flux.fromIterable(bestilling.getHenvendelse().getMeldinger())
                                    .flatMap(melding -> Mono.zip(
                                            Mono.just(mapperFacade.map(melding, HenvendelseMeldingRequest.class, context)),
                                            Mono.just(melding),
                                            Mono.just(tuple.getT3()))),
                            Flux.fromIterable(bestilling.getHenvendelse().getSamtalereferater())
                                    .flatMap(referat -> Mono.zip(
                                            Mono.just(mapperFacade.map(referat, HenvendelseSamtalereferatRequest.class, context)),
                                            Mono.just(referat),
                                            Mono.just(tuple.getT3()))));
                })
                .flatMap(tuple -> {

                    if (tuple.getT3().getData().stream()
                            .noneMatch(data -> data.getMeldinger().getFirst().getMeldingsId().equals(tuple.getT2().getMeldingsId()))) {

                        return henvendelseConsumer.sendHenvendelse(tuple.getT1())
                                .map(status -> {
                                    if (status.getStatus().is2xxSuccessful()) {
                                        tuple.getT2().setMeldingsId(status.getMeldinger().getFirst().getMeldingsId());
                                        return "%s: OK".formatted(status.getType());
                                    } else {
                                        return "%s: Feil= %s".formatted(
                                                status.getType(), ErrorStatusDecoder.encodeStatus(status.getFeilmelding()));
                                    }
                                });
                    } else {
                        return Mono.just("%s: OK".formatted(tuple.getT1().getType()));
                    }
                })
                .distinct()
                .collect(Collectors.joining(", "))
                .flatMap(status -> transactionHelperService.persister(progress.getBestillingId(), bestilling)
                        .thenReturn(status))
                .flatMap(status -> oppdaterStatus(progress, status));
    }

    @Override
    public void release(List<String> identer) {

        personServiceConsumer.getPdlPersoner(identer)
                .flatMap(HenvendelseClient::getAktorId)
                .flatMap(henvendelseConsumer::getHenvendelse)
                .map(HenvendelseResponse::getData)
                .flatMap(Flux::fromIterable)
                .filter(henvendelse -> "MELDINGSKJEDE".equals(henvendelse.getHenvendelseType()))
                .map(HenvendelseResponse.Info::getKjedeId)
                .flatMap(henvendelseConsumer::deleteHenvendelse)
                .collectList()
                .subscribe(henvendelser -> log.info("Lukket %d henvendelse(r) i Salesforce"
                        .formatted(henvendelser.size())));
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
