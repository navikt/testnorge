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
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokarkivClient implements ClientRegister {

    private final DokarkivConsumer dokarkivConsumer;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final TransactionHelperService transactionHelperService;
    private final PersonServiceConsumer personServiceConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.just(bestilling)
                .filter(bestilling1 -> nonNull(bestilling1.getDokarkiv()))
                .map(RsDollyUtvidetBestilling::getDokarkiv)
                .flatMap(dokarkiv -> Flux.from(getPersonData(List.of(dollyPerson.getIdent()))
                                .map(person -> buildRequest(dokarkiv, person))
                                .flatMap(request -> dokarkivConsumer.getEnvironments()
                                        .flatMapIterable(env -> env)
                                        .filter(env -> bestilling.getEnvironments().contains(env))
                                        .flatMap(env ->
                                                !transaksjonMappingService.existAlready(DOKARKIV,
                                                        dollyPerson.getIdent(), env, bestilling.getId()) || isOpprettEndre ?

                                                        dokarkivConsumer.postDokarkiv(env, request)
                                                                .map(status ->
                                                                        getStatus(dollyPerson.getIdent(),
                                                                                bestilling.getId(), status)) :

                                                        Mono.just(env + ":OK")
                                        ))
                                .collect(Collectors.joining(",")))
                        .map(status -> futurePersist(progress, status)));
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress,
                    BestillingProgress::getDokarkivStatus,
                    BestillingProgress::setDokarkivStatus, status);
            return progress;
        };
    }

    private String getStatus(String ident, Long bestillingId, DokarkivResponse response) {

        log.info("Dokarkiv response {} for ident {}", response, ident);

        if (isNull(response)) {
            return null;
        }

        if (isBlank(response.getFeilmelding())) {

            saveTransaksjonId(response, ident, bestillingId, response.getMiljoe());
            return response.getMiljoe() + ":OK";

        } else {

            return String.format("%s:FEIL=Teknisk feil se logg! %s", response.getMiljoe(),
                    isNotBlank(response.getFeilmelding()) ?
                            ErrorStatusDecoder.encodeStatus(errorStatusDecoder.getStatusMessage(response.getFeilmelding())) :
                            "UKJENT");
        }
    }

    @Override
    public void release(List<String> identer) {

        // Sletting er ikke støttet
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(List<String> identer) {

        return personServiceConsumer.getPdlPersoner(identer)
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()));
    }

    private DokarkivRequest buildRequest(RsDokarkiv rsDokarkiv, PdlPersonBolk.PersonBolk personBolk) {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("personBolk", personBolk);

        return mapperFacade.map(rsDokarkiv, DokarkivRequest.class, context);
    }

    private void saveTransaksjonId(DokarkivResponse response, String ident, Long bestillingId, String miljoe) {

        log.info("Lagrer transaksjon for {} i {} ", ident, miljoe);

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(JoarkTransaksjon.builder()
                                .journalpostId(response.getJournalpostId())
                                .dokumentInfoId(response.getDokumenter().get(0).getDokumentInfoId())
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
