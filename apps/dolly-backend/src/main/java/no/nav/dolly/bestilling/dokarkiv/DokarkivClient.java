package no.nav.dolly.bestilling.dokarkiv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.bestilling.dokarkiv.domain.JoarkTransaksjon;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.config.ApplicationConfig;
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
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
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
    private final TransactionHelperService transactionHelperService;
    private final TransaksjonMappingService transaksjonMappingService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.just(bestilling)
                .filter(bestilling1 -> nonNull(bestilling1.getDokarkiv()))
                .map(RsDollyUtvidetBestilling::getDokarkiv)
                .flatMap(dokarkiv -> Flux.from(getPersonData(dollyPerson.getIdent())
                                .flatMap(person -> getFilteredMiljoer(bestilling.getEnvironments())
                                        .flatMapMany(miljoer -> Flux.fromIterable(miljoer)
                                                .flatMap(env -> buildRequest(dokarkiv, person, progress.getBestilling().getId())
                                                        .flatMap(request ->
                                                                !transaksjonMappingService.existAlready(DOKARKIV,
                                                                        dollyPerson.getIdent(), env, bestilling.getId()) || isOpprettEndre ?
                                                                        dokarkivConsumer.postDokarkiv(env, request)
                                                                                .map(status ->
                                                                                        getStatus(dollyPerson.getIdent(),
                                                                                                bestilling.getId(), status)) :
                                                                        Mono.just(env + ":OK")
                                                        ))
                                                .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                                                .onErrorResume(error -> getErrors(error, miljoer))
                                        )))
                        .collect(Collectors.joining(","))
                        .map(status -> futurePersist(progress, status)));
    }

    private Flux<String> getErrors(Throwable error, List<String> miljoer) {

        return Flux.fromIterable(miljoer)
                .map(miljoe -> "%s:%s".formatted(miljoe, encodeStatus(WebClientFilter.getMessage(error))));
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress,
                    BestillingProgress::getDokarkivStatus,
                    BestillingProgress::setDokarkivStatus, status);
            return progress;
        };
    }

    private Mono<List<String>> getFilteredMiljoer(Set<String> miljoer) {

        return dokarkivConsumer.getEnvironments()
                .flatMapMany(Flux::fromIterable)
                .filter(miljoer::contains)
                .collectList();
    }

    private String getStatus(String ident, Long bestillingId, DokarkivResponse response) {

        log.info("Dokarkiv response {} for ident {}", response, ident);

        if (isNull(response)) {
            return "UKJENT:Intet svar";
        }

        if (isBlank(response.getFeilmelding())) {

            saveTransaksjonId(response, ident, bestillingId, response.getMiljoe());
            return response.getMiljoe() + ":OK";

        } else {

            return String.format("%s:FEIL=Teknisk feil se logg! %s", response.getMiljoe(),
                    isNotBlank(response.getFeilmelding()) ?
                            encodeStatus(errorStatusDecoder.getStatusMessage(response.getFeilmelding())) :
                            "UKJENT");
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

        var context = new MappingContext.Factory().getContext();
        context.setProperty("personBolk", personBolk);
        context.setProperty("dokumenter", dokumentService.getDokumenter(bestillingId));

        return Mono.just(mapperFacade.map(rsDokarkiv, DokarkivRequest.class, context));
    }

    private void saveTransaksjonId(DokarkivResponse response, String ident, Long bestillingId, String miljoe) {

        log.info("Lagrer transaksjon for {} i {} ", ident, miljoe);

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(JoarkTransaksjon.builder()
                                .journalpostId(response.getJournalpostId())
                                .dokumentInfoId(response.getDokumenter().getFirst().getDokumentInfoId())
                                .build()))
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

}
