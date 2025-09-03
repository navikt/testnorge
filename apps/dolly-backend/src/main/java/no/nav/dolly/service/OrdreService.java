package no.nav.dolly.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingClient;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrdreStatus;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.mapper.BestillingPdlOrdreStatusMapper;
import no.nav.dolly.repository.IdentRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdreService {

    private static final String IKKE_FUNNET = "Testperson med ident %s ble ikke funnet";
    private final IdentRepository identRepository;
    private final PdlDataConsumer pdlDataConsumer;
    private final PersonServiceClient personServiceClient;
    private final TpsMessagingClient tpsMessagingClient;
    private final PensjonforvalterClient pensjonforvalterClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public Mono<RsOrdreStatus> sendOrdre(String ident) {

        return identRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException(String.format(IKKE_FUNNET, ident))))
                .map(testident -> BestillingProgress.builder()
                        .ident(ident)
                        .master(testident.getMaster())
                        .bestillingId(1L)
                        .build())
                .flatMap(progress -> Mono.just(DollyPerson.builder()
                                .ident(ident)
                                .master(progress.getMaster())
                                .isOrdre(true)
                                .build())
                        .map(dollyperson -> sendOrdre(dollyperson, progress)
                                .flatMap(pdlOrdreResponse ->
                                        personServiceClient.syncPerson(dollyperson, progress)
                                                .map(BestillingProgress::isPdlSync)
                                                .filter(BooleanUtils::isTrue)
                                                .flatMapMany(opprettet -> Flux.merge(
                                                        pensjonforvalterClient.gjenopprett(new RsDollyUtvidetBestilling(), dollyperson, progress, false),
                                                        tpsMessagingClient.gjenopprett(new RsDollyUtvidetBestilling(), dollyperson, progress, false)))
                                                .collectList()
                                                .map(status -> RsOrdreStatus.builder()
                                                        .status(BestillingPdlOrdreStatusMapper.buildPdlOrdreStatusMap(List.of(progress), objectMapper))
                                                        .build())))
                        .flatMap(Mono::from));
    }

    private Mono<BestillingProgress> sendOrdre(DollyPerson dollyPerson, BestillingProgress progress) {

        return pdlDataConsumer.sendOrdre(dollyPerson.getIdent(), false)
                .map(response -> {
                    progress.setPdlOrdreStatus(response.getStatus().is2xxSuccessful() ?
                            response.getJsonNode() : response.getFeilmelding());
                    log.atLevel(response.getStatus().is2xxSuccessful() ? Level.INFO : Level.ERROR)
                            .log("Pdl-forvalter status: {}", progress.getPdlOrdreStatus());
                    return progress;
                });
    }
}
