package no.nav.dolly.bestilling.inntektsmelding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.dolly.bestilling.inntektsmelding.domain.TransaksjonMappingDTO;
import no.nav.dolly.bestilling.inntektsmelding.dto.TransaksjonmappingIdDTO;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.consumer.dokumentarkiv.SafConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTKMELD;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektsmeldingClient implements ClientRegister {

    private static final String STATUS_FMT = "%s:%s";

    private final ApplicationConfig applicationConfig;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final InntektsmeldingConsumer inntektsmeldingConsumer;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;
    private final SafConsumer safConsumer;
    private final TransactionHelperService transactionHelperService;
    private final TransaksjonMappingService transaksjonMappingService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Mono.just(bestilling)
                .filter(RsDollyBestilling::isExistInntekstsmelding)
                .map(RsDollyBestilling::getInntektsmelding)
                .flatMap(inntektsmelding -> Flux.fromIterable(bestilling.getEnvironments())
                                .flatMap(miljoe -> isOpprettDokument(miljoe, dollyPerson.getIdent(), bestilling.getId(), isOpprettEndre)
                                        .flatMapMany(isOpprett -> isTrue(isOpprett) ?
                                                postInntektsmelding(bestilling.getInntektsmelding(), dollyPerson.getIdent(), miljoe, bestilling.getId()) :
                                                Mono.just(miljoe + ":OK")))
                                .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                                .onErrorResume(error -> getErrors(error, bestilling.getEnvironments()))
                                .collect(Collectors.joining(","))
                        .flatMap(status -> futurePersist(progress, status)));
    }

    private Mono<Boolean> isOpprettDokument(String miljoe, String ident, Long bestillingId, Boolean isOpprettEndre) {

        if (isTrue(isOpprettEndre)) {
            return Mono.just(true);
        }

        return transaksjonMappingService.getTransaksjonMapping(INNTKMELD.name(), ident, bestillingId)
                .doOnNext(transaksjonMapping -> log.info("Eksisterende transaksjonmapping {}", transaksjonMapping))
                .filter(transaksjonMapping -> miljoe.equals(transaksjonMapping.getMiljoe()))
                .map(transaksjon -> fromJson(transaksjon.getTransaksjonId()))
                .doOnNext(transaksjon -> log.info("Verdi fra transaksjonmapping {}", transaksjon))
                .flatMap(transaksjon -> nonNull(transaksjon.getDokument()) ?
                        safConsumer.getDokument(miljoe, transaksjon.getDokument().getJournalpostId(),
                                        transaksjon.getDokument().getDokumentInfoId(), "ORIGINAL")
                                .map(status -> isBlank(status.getFeilmelding()) && isNotBlank(status.getDokument())) :
                        Mono.just(false))
                .doOnNext(status -> log.info("Dokument eksisterer {}", status))
                .reduce(true, (a, b) -> a && b)
                .flatMap(finnes -> {
                    if (isFalse(finnes)) {
                        return transaksjonMappingService.delete(ident, miljoe, INNTKMELD.name(), bestillingId)
                                .thenReturn(finnes);
                    }
                    return Mono.just(false);
                })
                .map(BooleanUtils::isFalse)
                .doOnNext(ok -> log.info("Opprett dokument {}", ok))
                .defaultIfEmpty(true);
    }

    private Flux<String> getErrors(Throwable error, Set<String> environments) {

        return Flux.fromIterable(environments)
                .map(env -> STATUS_FMT.formatted(env,
                        encodeStatus(WebClientError.describe(error).getMessage())));
    }

    @Override
    public void release(List<String> identer) {

        // Inntektsmelding mangler pt. sletting
    }

    private Mono<BestillingProgress> futurePersist(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress,
                BestillingProgress::getInntektsmeldingStatus,
                BestillingProgress::setInntektsmeldingStatus, status);
    }

    private Mono<String> postInntektsmelding(

            RsInntektsmelding inntektsmelding,
            String ident,
            String miljoe,
            Long bestillingid) {

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", ident);
        context.setProperty("miljoe", miljoe);
        var inntektsmeldingRequest = mapperFacade.map(inntektsmelding, InntektsmeldingRequest.class, context);

        return inntektsmeldingConsumer
                .postInntektsmelding(inntektsmeldingRequest)
                .flatMap(response -> {
                    if (isBlank(response.getError())) {
                        return transaksjonMappingService.saveAll(getMapping(response, ident, bestillingid, miljoe, inntektsmeldingRequest))
                                .thenReturn(miljoe + ":OK");
                    } else {
                        log.error("Feilet å legge inn person: {} til Inntektsmelding miljø: {} feilmelding {}",
                                inntektsmeldingRequest.getArbeidstakerFnr(), miljoe, response.getError());

                        return Mono.just(String.format(STATUS_FMT, miljoe,
                                errorStatusDecoder.getErrorText(response.getStatus(), response.getError())));
                    }
                });
    }

    private List<TransaksjonMapping> getMapping(InntektsmeldingResponse response, String ident, Long bestillingid,
                                                String miljoe, InntektsmeldingRequest inntektsmeldingRequest) {

        return response
                .getDokumenter()
                .stream()
                .map(dokument ->
                        TransaksjonMapping
                                .builder()
                                .ident(ident)
                                .bestillingId(bestillingid)
                                .transaksjonId(toJson(TransaksjonMappingDTO.builder()
                                        .request(InntektsmeldingRequest.builder()
                                                .arbeidstakerFnr(ident)
                                                .inntekter(singletonList(inntektsmeldingRequest.getInntekter()
                                                        .get(response.getDokumenter().indexOf(dokument))))
                                                .joarkMetadata(inntektsmeldingRequest.getJoarkMetadata())
                                                .miljoe(miljoe)
                                                .build())
                                        .dokument(dokument)
                                        .build()))
                                .datoEndret(LocalDateTime.now())
                                .miljoe(miljoe)
                                .system(INNTKMELD.name())
                                .build()
                )
                .toList();
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere dokument fra inntektsmelding", e);
        }
        return null;
    }

    private TransaksjonmappingIdDTO fromJson(JsonNode transaksjon) {

        try {
            if (nonNull(transaksjon)) {
                var transaksjonMapping = objectMapper.treeToValue(transaksjon, TransaksjonmappingIdDTO.class);
                if (isNotBlank(transaksjonMapping.getDokumentInfoId())) {
                    transaksjonMapping.setDokument(TransaksjonmappingIdDTO.Dokument.builder()
                            .dokumentInfoId(transaksjonMapping.getDokumentInfoId())
                            .journalpostId(transaksjonMapping.getJournalpostId())
                            .build());
                }
                return transaksjonMapping;
            } else {
                return new TransaksjonmappingIdDTO();
            }
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for inntektsmelding", e);
            return new TransaksjonmappingIdDTO();
        }
    }
}
