package no.nav.dolly.bestilling.yrkesskade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.yrkesskade.dto.ResponseDTO;
import no.nav.dolly.bestilling.yrkesskade.dto.ResponseDTO.Status;
import no.nav.dolly.bestilling.yrkesskade.dto.YrkesskadeResponseDTO;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static no.nav.dolly.domain.resultset.SystemTyper.YRKESSKADE;
import static no.nav.dolly.util.DateZoneUtil.CET;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getYrkesskader().isEmpty()) {

            var index = new AtomicInteger(0);
            return oppdaterStatus(progress, "Yrkesskade#1:%s".formatted(ErrorStatusDecoder.getInfoVenter(YRKESSKADE.getBeskrivelse())))
                    .flatMap(progres1 -> yrkesskadeConsumer.hentSaksoversikt(dollyPerson.getIdent())
                            .map(resultat -> !resultat.getSaker().isEmpty())
                            .map(eksisterendeSak -> !eksisterendeSak || isOpprettEndre)
                            .flatMap(nysak -> TRUE.equals(nysak) ?
                                    personServiceConsumer.getPdlPersoner(List.of(dollyPerson.getIdent()))
                                            .doOnNext(personBolk -> log.info("Hentet pdlPersonBolk"))
                                            .flatMap(personbolk -> Flux.fromIterable(bestilling.getYrkesskader())
                                                    .map(yrkesskade -> {
                                                        var context = MappingContextUtils.getMappingContext();
                                                        context.setProperty("ident", dollyPerson.getIdent());
                                                        context.setProperty("personBolk", personbolk);
                                                        return mapper.map(yrkesskade, YrkesskadeRequest.class, context);
                                                    })
                                                    .flatMap(yrkesskade -> yrkesskadeConsumer.lagreYrkesskade(yrkesskade)
                                                            .flatMap(status -> lagreTransaksjon(status, yrkesskade, progress.getBestillingId()))))
                                            .map(status -> encodeStatus(status, index.incrementAndGet()))
                                            .collectList()
                                            .flatMap(resultat -> oppdaterStatus(progress, resultat)) :

                                    oppdaterStatus(progress, List.of(ResponseDTO.builder()
                                            .id(index.incrementAndGet())
                                            .status(Status.OK)
                                            .build()))
                            ));
        }

        return Mono.empty();
    }

    @Override
    public void release(List<String> identer) {
        // Er ikke støttet
    }

    private ResponseDTO encodeStatus(YrkesskadeResponseDTO status, int index) {

        String melding;

        if (status.getStatus().is2xxSuccessful()) {
            melding = null;
        } else if (isNotBlank(status.getMelding())) {
            melding = status.getMelding();
        } else {
            melding = status.getStatus().toString();
        }

        return ResponseDTO.builder()
                .id(index)
                .status(status.getStatus().is2xxSuccessful() ? Status.OK : Status.FEIL)
                .melding(melding)
                .build();
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setYrkesskadeStatus, status);
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, List<ResponseDTO> response) {

        return oppdaterStatus(progress, response.stream()
                .map(entry -> "Yrkesskade#%d:%s".formatted(entry.getId(),
                        entry.getStatus() == Status.OK ? "OK" :
                                ErrorStatusDecoder.encodeStatus("FEIL: %s".formatted(entry.getMelding()))))
                .collect(Collectors.joining(",")));
    }

    private Mono<YrkesskadeResponseDTO> lagreTransaksjon(YrkesskadeResponseDTO status, YrkesskadeRequest request,
                                                   Long bestillingId) {

        if (status.getStatus().is2xxSuccessful()) {
            return transaksjonMappingService.save(TransaksjonMapping.builder()
                    .bestillingId(bestillingId)
                    .transaksjonId(toJson(request))
                    .ident(request.getSkadelidtIdentifikator())
                    .datoEndret(LocalDateTime.now(CET))
                    .system(YRKESSKADE.name())
                    .build())
                    .thenReturn(status);
        }
        return Mono.just(status);
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
