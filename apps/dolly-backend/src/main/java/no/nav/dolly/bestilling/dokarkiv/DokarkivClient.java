package no.nav.dolly.bestilling.dokarkiv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.bestilling.dokarkiv.domain.JoarkTransaksjon;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
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
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselSlutt;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVenterTekst;
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
    private final PdlPersonConsumer pdlPersonConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getDokarkiv())) {

            progress.setDokarkivStatus(bestilling.getEnvironments().stream()
                    .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getInfoVenter("JOARK"))))
                    .collect(Collectors.joining(",")));
            transactionHelperService.persister(progress);

            var bestillingId = progress.getBestilling().getId();
            personServiceConsumer.getPdlSyncReady(dollyPerson.getHovedperson())
                    .flatMap(isReady -> (isReady ?
                            getPersonData(List.of(dollyPerson.getHovedperson()))
                                    .map(person -> buildRequest(bestilling.getDokarkiv(), person))
                                    .flatMap(request -> dokarkivConsumer.getEnvironments()
                                            .flatMapIterable(env -> env)
                                            .filter(env -> bestilling.getEnvironments().contains(env))
                                            .filter(env -> !transaksjonMappingService.existAlready(DOKARKIV,
                                                    dollyPerson.getHovedperson(), env) || isOpprettEndre)
                                            .flatMap(env -> dokarkivConsumer.postDokarkiv(env, request)
                                                    .map(status -> getStatus(dollyPerson.getHovedperson(), bestillingId, status))))
                                    .collect(Collectors.joining(",")) :

                            Mono.just(bestilling.getEnvironments().stream()
                                    .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getVarselSlutt("JOARK"))))
                                    .collect(Collectors.joining(",")))))

                    .subscribe(resultat -> {
                        progress.setDokarkivStatus(resultat);
                        transactionHelperService.persister(progress);
                    });
        }

        return Flux.just();
    }

    private String getStatus(String ident, Long bestillingId, DokarkivResponse response) {

        if (nonNull(response) && isBlank(response.getFeilmelding())) {

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

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getDokarkiv()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getDokarkivStatus()) &&
                                !entry.getDokarkivStatus().contains(getVenterTekst()));
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(List<String> identer) {

        return pdlPersonConsumer.getPdlPersoner(identer)
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
