package no.nav.dolly.bestilling.sykemelding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.SykemeldingTransaksjon;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.Norg2EnhetResponse;
import no.nav.dolly.bestilling.sykemelding.dto.SykemeldingResponse;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SYKEMELDING;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselSlutt;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingClient implements ClientRegister {

    private final SykemeldingConsumer sykemeldingConsumer;
    private final SyntSykemeldingConsumer syntSykemeldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransaksjonMappingService transaksjonMappingService;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;
    private final PersonServiceConsumer personServiceConsumer;
    private final TransactionHelperService transactionHelperService;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final KodeverkConsumer kodeverkConsumer;
    private final Norg2Consumer norg2Consumer;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getSykemelding())) {

            if (transaksjonMappingService.existAlready(SYKEMELDING, dollyPerson.getHovedperson(), null) && !isOpprettEndre) {
                setProgress(progress, "OK");

            } else {
                setProgress(progress, encodeStatus(getInfoVenter("Sykemelding")));
                long bestillingId = progress.getBestilling().getId();

                return Flux.from(personServiceConsumer.getPdlSyncReady(dollyPerson.getHovedperson())
                        .flatMap(isSync -> {
                            if (isTrue(isSync)) {
                                setProgress(progress, "Info: Venter på generering av sykemelding ...");
                                return getPerson(dollyPerson.getHovedperson())
                                        .flatMap(persondata -> Mono.zip(kodeverkConsumer.getKodeverkByName("Postnummer"),
                                                        getNorgenhet(persondata))
                                                .flatMap(zip -> Flux.concat(postSyntSykemelding(bestilling, persondata),
                                                                postDetaljertSykemelding(bestilling, persondata,
                                                                        zip.getT1(), zip.getT2()))
                                                        .filter(Objects::nonNull)
                                                        .map(status -> saveTransaksjonId(status, bestillingId))
                                                        .map(this::getStatus)
                                                        .collect(Collectors.joining())))
                                        .collect(Collectors.joining());
                            } else {
                                return Mono.just(encodeStatus(getVarselSlutt("Sykemelding")));
                            }
                        })
                        .map(status -> futurePersist(progress, status)));
            }
        }
        return Flux.empty();
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            progress.setSykemeldingStatus(status);
            transactionHelperService.persister(progress);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        // Sletting er ikke støttet
    }

    private String getStatus(SykemeldingResponse status) {

        log.info("Sykemelding response for {} mottatt, status: {}", status.getIdent(), status.getStatus());
        return status.getStatus().is2xxSuccessful() ? "OK" :
                errorStatusDecoder.getErrorText(status.getStatus(), status.getAvvik());
    }

    private void setProgress(BestillingProgress progress, String status) {

        progress.setSykemeldingStatus(status);
        transactionHelperService.persister(progress);
    }

    private Flux<PdlPersonBolk.Data> getPerson(String ident) {

        return pdlPersonConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData);
    }

    private Mono<Norg2EnhetResponse> getNorgenhet(PdlPersonBolk.Data persondata) {

        var geografiskOmrade = persondata.getHentGeografiskTilknytningBolk().stream()
                .map(PdlPersonBolk.GeografiskTilknytningBolk::getGeografiskTilknytning)
                .map(geografiskTilknytning -> isNotBlank(geografiskTilknytning.getGtType()) ?
                        switch (geografiskTilknytning.getGtType()) {
                            case "KOMMUNE" -> geografiskTilknytning.getGtKommune();
                            case "BYDEL" -> geografiskTilknytning.getGtBydel();
                            default -> null;
                        } : null)
                .collect(Collectors.joining());

        return isNotBlank(geografiskOmrade) ? norg2Consumer.getNorgEnhet(geografiskOmrade) : Mono.empty();
    }

    private Mono<SykemeldingResponse> postDetaljertSykemelding(RsDollyUtvidetBestilling bestilling,
                                                               PdlPersonBolk.Data persondata,
                                                               Map<String, String> postnummer,
                                                               Norg2EnhetResponse norg2Enhet) {

        if (nonNull(bestilling.getSykemelding().getDetaljertSykemelding())) {

            var detaljertSykemeldingRequest =
                    mapperFacade.map(bestilling.getSykemelding().getDetaljertSykemelding(),
                            DetaljertSykemeldingRequest.class);

            var context = new MappingContext.Factory().getContext();
            context.setProperty("postnummer", postnummer);
            context.setProperty("norg2Enhet", norg2Enhet);

            detaljertSykemeldingRequest.setPasient(mapperFacade.map(persondata,
                    DetaljertSykemeldingRequest.Pasient.class, context));

            return sykemeldingConsumer.postDetaljertSykemelding(detaljertSykemeldingRequest)
                    .map(status -> {
                        status.setDetaljertSykemeldingRequest(detaljertSykemeldingRequest);
                        status.setIdent(detaljertSykemeldingRequest.getPasient().getIdent());
                        return status;
                    });

        } else {
            return Mono.empty();
        }
    }

    private Mono<SykemeldingResponse> postSyntSykemelding(RsDollyUtvidetBestilling bestilling, PdlPersonBolk.Data persondata) {

        if (nonNull(bestilling.getSykemelding().getSyntSykemelding())) {

            var context = new MappingContext.Factory().getContext();
            context.setProperty("persondata", persondata);
            var syntSykemeldingRequest =
                    mapperFacade.map(bestilling.getSykemelding().getSyntSykemelding(), SyntSykemeldingRequest.class, context);

            return syntSykemeldingConsumer.postSyntSykemelding(syntSykemeldingRequest)
                    .map(status -> {
                        status.setSyntSykemeldingRequest(syntSykemeldingRequest);
                        status.setIdent(syntSykemeldingRequest.getIdent());
                        return status;
                    });

        } else {
            return Mono.empty();
        }
    }

    private SykemeldingResponse saveTransaksjonId(SykemeldingResponse sykemelding, Long bestillingId) {

        if (sykemelding.getStatus().is2xxSuccessful()) {
            if (nonNull(sykemelding.getSyntSykemeldingRequest())) {

                transaksjonMappingService.save(TransaksjonMapping.builder()
                        .ident(sykemelding.getSyntSykemeldingRequest().getIdent())
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(SykemeldingTransaksjon.builder()
                                .orgnummer(Optional.of(sykemelding.getSyntSykemeldingRequest().getOrgnummer())
                                        .orElse("NA"))
                                .arbeidsforholdId(Optional.of(sykemelding.getSyntSykemeldingRequest().getArbeidsforholdId())
                                        .orElse("1"))
                                .build()))
                        .datoEndret(LocalDateTime.now())
                        .system(SYKEMELDING.name())
                        .build());
            } else {
                transaksjonMappingService.save(TransaksjonMapping.builder()
                        .ident(sykemelding.getDetaljertSykemeldingRequest().getPasient().getIdent())
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(Optional.of(sykemelding.getDetaljertSykemeldingRequest().getMottaker())
                                .orElse(DetaljertSykemeldingRequest.Organisasjon.builder()
                                        .orgNr("NA")
                                        .build())
                                .getOrgNr()))
                        .datoEndret(LocalDateTime.now())
                        .system(SYKEMELDING.name())
                        .build());
            }
        }
        return sykemelding;
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for sykemelding");
        }
        return null;
    }
}
