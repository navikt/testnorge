package no.nav.dolly.bestilling.yrkesskade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.yrkesskade.dto.ResponseDTO;
import no.nav.dolly.bestilling.yrkesskade.dto.ResponseDTO.Status;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static no.nav.dolly.domain.resultset.SystemTyper.YRKESSKADE;

@Slf4j
@Service
@RequiredArgsConstructor
public class YrkesskadeClient implements ClientRegister {

    private final MapperFacade mapper;
    private final ObjectMapper objectMapper;
    private final YrkesskadeConsumer yrkesskadeConsumer;
    private final TransactionHelperService transactionHelperService;
    private final TransaksjonMappingService transaksjonMappingService;
    private final PersonServiceConsumer personServiceConsumer;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var index = new AtomicInteger(0);

        return Flux.just(bestilling)
                .filter(best -> !best.getYrkesskader().isEmpty())
                .filter(best -> isOpprettEndre)
                .flatMap(best -> personServiceConsumer.getPdlPersoner(List.of(dollyPerson.getIdent())))
                .doOnNext(resultat -> log.info("Hentet pdlPersonBolk"))
                .flatMap(personbolk -> Flux.fromIterable(bestilling.getYrkesskader())
                        .map(yrkesskade -> {
                            var context = MappingContextUtils.getMappingContext();
                            context.setProperty("ident", dollyPerson.getIdent());
                            context.setProperty("personBolk", personbolk);
                            return mapper.map(yrkesskade, YrkesskadeRequest.class, context);
                        })
                        .flatMap(yrkesskade -> yrkesskadeConsumer.lagreYrkesskade(yrkesskade)
                                .map(status -> lagreTransaksjon(status, yrkesskade, progress.getBestilling().getId())))
                        .map(status -> encodeStatus(status, index.incrementAndGet()))
                        .collectList()
                        .map(resultat -> futurePersist(progress, resultat)));
    }

    @Override
    public void release(List<String> identer) {
        // Er ikke støttet
    }

    private ResponseDTO encodeStatus(ResponseEntity<String> status, int index) {

        return ResponseDTO.builder()
                .id(index)
                .status(status.getStatusCode().is2xxSuccessful() ? Status.OK : Status.FEIL)
                .melding(status.getBody())
                .build();
    }

    private ClientFuture futurePersist(BestillingProgress progress, List<ResponseDTO> response) {

        var status = response.stream()
                .map(entry -> "Yrkesskade#%d:%s".formatted(entry.getId(),
                        entry.getStatus() == Status.OK ? "OK" :
                                 ErrorStatusDecoder.encodeStatus("FEIL: %s".formatted(entry.getMelding()))))
                .collect(Collectors.joining(","));

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setYrkesskadeStatus, status);
            return progress;
        };
    }

    private ResponseEntity<String> lagreTransaksjon(ResponseEntity<String> status, YrkesskadeRequest request,
                                                    Long bestillingId) {

        if (status.getStatusCode().is2xxSuccessful()) {
            transaksjonMappingService.save(TransaksjonMapping.builder()
                    .bestillingId(bestillingId)
                    .transaksjonId(toJson(request))
                    .ident(request.getSkadelidtIdentifikator())
                    .datoEndret(LocalDateTime.now())
                    .system(YRKESSKADE.name())
                    .build());
        }
        return status;
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
