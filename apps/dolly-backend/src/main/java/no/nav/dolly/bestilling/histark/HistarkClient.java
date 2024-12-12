package no.nav.dolly.bestilling.histark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.bestilling.histark.domain.HistarkResponse;
import no.nav.dolly.bestilling.histark.domain.JoarkHistarkTransaksjon;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DokumentService;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.HISTARK;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistarkClient implements ClientRegister {

    private final HistarkConsumer histarkConsumer;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final TransactionHelperService transactionHelperService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final DokumentService dokumentService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getHistark())) {

            return Flux.just(dollyPerson.getIdent())
                    .map(person -> buildRequest(bestilling.getHistark(), person, progress.getBestilling().getId()))
                    .flatMap(request -> !transaksjonMappingService.existAlready(HISTARK,
                            dollyPerson.getIdent(), "NA", bestilling.getId()) || isOpprettEndre ?

                            histarkConsumer.postHistark(request)
                                    .mapNotNull(status -> getStatus(dollyPerson.getIdent(), bestilling.getId(), status)) :

                            Mono.just("OK")
                    )
                    .map(status -> futurePersist(progress, status));
        }

        return Flux.empty();
    }

    @Override
    public void release(List<String> identer) {

        // Sletting er ikke støttet
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setHistarkStatus, status);
            return progress;
        };
    }

    private String getStatus(String ident, Long bestillingId, HistarkResponse response) {

        log.info("Histark response {} mottatt for ident {}", response, ident);

        if (isNull(response)) {
            return null;
        }

        if (isBlank(response.getFeilmelding())) {

            saveTransaksjonId(response.getHistarkId(), ident, bestillingId);
            return "OK";

        } else {

            return String.format("FEIL=Teknisk feil se logg! %s",
                    isNotBlank(response.getFeilmelding()) ?
                            ErrorStatusDecoder.encodeStatus(errorStatusDecoder.getStatusMessage(response.getFeilmelding())) :
                            "UKJENT");
        }
    }

    private HistarkRequest buildRequest(RsHistark rsHistark, String ident, Long bestillingId) {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("personIdent", ident);
        context.setProperty("dokumenter", dokumentService.getDokumenter(bestillingId));

        return mapperFacade.map(rsHistark, HistarkRequest.class, context);
    }

    private void saveTransaksjonId(String histarkId, String ident, Long bestillingId) {

        log.info("Lagrer transaksjon for ident {}", ident);

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(JoarkHistarkTransaksjon.builder()
                                .dokumentInfoId(histarkId)
                                .build()))
                        .datoEndret(LocalDateTime.now())
                        .miljoe("NA")
                        .system(HISTARK.name())
                        .build());
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for histark", e);
        }
        return null;
    }
}
