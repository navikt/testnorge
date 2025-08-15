package no.nav.dolly.bestilling.dokarkiv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.bestilling.dokarkiv.domain.JoarkTransaksjon;
import no.nav.dolly.bestilling.dokarkiv.dto.TransaksjonIdDTO;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.consumer.dokumentarkiv.SafConsumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DokumentService;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokarkivClient implements ClientRegister {

    private final ApplicationConfig applicationConfig;
    private final DokarkivConsumer dokarkivConsumer;
    private final DokumentService dokumentService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;
    private final PersonServiceConsumer personServiceConsumer;
    private final SafConsumer safConsumer;
    private final TransactionHelperService transactionHelperService;
    private final TransaksjonMappingService transaksjonMappingService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (bestilling.getDokarkiv().isEmpty()) {
            return Mono.empty();
        }

        return oppdaterStatus(progress, bestilling.getEnvironments().stream()
                .map(miljo -> "%s:%s".formatted(miljo, getInfoVenter(DOKARKIV.name())))
                .collect(Collectors.joining(",")))
                .then(getPersonData(dollyPerson.getIdent())
                .flatMap(person -> getFilteredMiljoer(bestilling.getEnvironments())
                        .flatMapMany(miljoer -> Flux.fromIterable(miljoer)
                                .flatMap(miljoe -> isOpprettDokument(miljoe, dollyPerson.getIdent(), bestilling.getId(), isOpprettEndre)
                                        .flatMap(isOpprettDokument -> isTrue(isOpprettDokument)
                                                ?
                                                Flux.fromIterable(bestilling.getDokarkiv())
                                                        .flatMap(dokarkiv ->
                                                                buildRequest(dokarkiv, person, bestilling.getId())
                                                                        .flatMap(request -> dokarkivConsumer.postDokarkiv(miljoe, request)))
                                                        .collectList()
                                                        .map(status -> getStatus(dollyPerson.getIdent(), bestilling.getId(), status))
                                                :
                                                Mono.just(miljoe + ":OK")
                                        )
                                )
                                .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                                .onErrorResume(error -> getErrors(error, miljoer))
                        ))
                .collect(Collectors.joining(","))
                .flatMap(status -> oppdaterStatus(progress, status)));
    }

    private Mono<Boolean> isOpprettDokument(String miljoe, String ident, Long bestillingId, Boolean isOpprettEndre) {

        if (isTrue(isOpprettEndre)) {
            return Mono.just(true);
        }

        return transaksjonMappingService.getTransaksjonMapping(DOKARKIV.name(), ident, bestillingId)
                .doOnNext(transaksjonMapping -> log.info("Eksisterende transaksjonmapping {}", transaksjonMapping))
                .filter(transaksjonMapping -> transaksjonMapping.getMiljoe().equals(miljoe))
                .mapNotNull(transaksjon -> fromJson(transaksjon.getTransaksjonId()))
                .doOnNext(transaksjon -> log.info("Verdi fra transaksjonmapping {}", transaksjon))
                .flatMap(transaksjoner -> Flux.fromIterable(transaksjoner)
                        .flatMap(transaksjon -> safConsumer.getDokument(miljoe, transaksjon.getJournalpostId(),
                                transaksjon.getDokumentInfoId(), "ARKIV")))
                .map(status -> isBlank(status.getFeilmelding()) && isNotBlank(status.getDokument()))
                .doOnNext(status -> log.info("Dokument eksisterer {}", status))
                .reduce(true, (a, b) -> a && b)
                .flatMap(status -> {
                    if (isFalse(status)) {
                        return transaksjonMappingService.delete(ident, miljoe, DOKARKIV.name(), bestillingId)
                                .thenReturn(status);
                    }
                    return Mono.just(status);
                })
                .map(BooleanUtils::isFalse)
                .doOnNext(ok -> log.info("Opprett dokument {}", ok))
                .defaultIfEmpty(true);
    }

    private Flux<String> getErrors(Throwable error, List<String> miljoer) {

        return Flux.fromIterable(miljoer)
                .map(miljoe -> "%s:%s".formatted(miljoe, encodeStatus(WebClientError.describe(error).getMessage())));
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress,
                BestillingProgress::getDokarkivStatus,
                BestillingProgress::setDokarkivStatus, status);
    }

    private Mono<List<String>> getFilteredMiljoer(Set<String> miljoer) {

        return dokarkivConsumer.getEnvironments()
                .flatMapMany(Flux::fromIterable)
                .filter(miljoer::contains)
                .collectList();
    }

    private String getStatus(String ident, Long bestillingId, List<DokarkivResponse> response) {

        log.info("Dokarkiv response {} for ident {}", response, ident);

        if (isNull(response)) {
            return "UKJENT:Intet svar";
        }

        if (response.stream().allMatch(arkiv -> isBlank(arkiv.getFeilmelding()))) {

            saveTransaksjonId(response, ident, bestillingId, response.getFirst().getMiljoe());
            return response.getFirst().getMiljoe() + ":OK";

        } else {

            return String.format("%s:FEIL=Teknisk feil se logg! %s", response.getFirst().getMiljoe(),

                    response.stream()
                            .filter(arkiv -> isNotBlank(arkiv.getFeilmelding()))
                            .map(arkiv -> encodeStatus(errorStatusDecoder.getStatusMessage(arkiv.getFeilmelding())))
                            .findFirst()
                            .orElse("UKJENT"));
        }
    }

    @Override
    public void release(List<String> identer) {

        // Sletting er ikke støttet
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(String ident) {

        return personServiceConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()));
    }

    private Mono<DokarkivRequest> buildRequest(RsDokarkiv rsDokarkiv, PdlPersonBolk.PersonBolk personBolk, Long bestillingId) {

        return dokumentService.getDokumenterByBestilling(bestillingId)
                .collectList()
                .map(dokumenter -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty("personBolk", personBolk);
                    context.setProperty("dokumenter", dokumenter);
                    return context;
                })
                .map(context -> mapperFacade.map(rsDokarkiv, DokarkivRequest.class, context));
    }

    private void saveTransaksjonId(List<DokarkivResponse> response, String ident, Long bestillingId, String miljoe) {

        log.info("Lagrer transaksjon for {} i {} ", ident, miljoe);

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(response.stream()
                                .map(arkiv -> JoarkTransaksjon.builder()
                                        .journalpostId(arkiv.getJournalpostId())
                                        .dokumentInfoId(arkiv.getDokumenter().getFirst().getDokumentInfoId())
                                        .build())
                                .toList()))
                        .datoEndret(LocalDateTime.now())
                        .miljoe(miljoe)
                        .system(DOKARKIV.name())
                        .build());
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for dokarkiv", e);
        }
        return null;
    }

    private List<TransaksjonIdDTO> fromJson(JsonNode transaksjon) {

        var transaksjoner = new ArrayList<TransaksjonIdDTO>();
        transaksjon.iterator()
                .forEachRemaining(node ->
                        {
                            try {
                                transaksjoner.add(objectMapper.treeToValue(node, TransaksjonIdDTO.class));
                            } catch (JsonProcessingException e) {
                                log.error("Feilet å konvertere transaksjonsId for dokarkiv", e);
                            }
                        }
                );
        return transaksjoner;
    }
}