package no.nav.dolly.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrdreStatus;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.mapper.BestillingPdlDataStatusMapper;
import no.nav.dolly.mapper.BestillingPensjonforvalterStatusMapper;
import no.nav.dolly.mapper.BestillingTpsMessagingStatusMapper;
import no.nav.dolly.repository.IdentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

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

    private static RsStatusRapport getStatus(List<RsStatusRapport> status) {

        return nonNull(status) ? status.stream().findFirst().orElse(null) : null;
    }

    @Transactional(readOnly = true)
    public RsOrdreStatus sendOrdre(String ident) {

        var testident = identRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(String.format(IKKE_FUNNET, ident)));

        return Flux.just(BestillingProgress.builder()
                        .ident(ident)
                        .master(testident.getMaster())
                        .bestilling(Bestilling.builder()
                                .id(1L)
                                .build())
                        .build())
                .flatMap(progress -> Flux.just(DollyPerson.builder()
                                .ident(ident)
                                .master(progress.getMaster())
                                .isOrdre(true)
                                .build())
                        .flatMap(dollyperson -> sendOrdre(dollyperson, progress)
                                .flatMap(pdlOrdreResponse ->
                                        personServiceClient.syncPerson(dollyperson, progress)
                                                .map(ClientFuture::get)
                                                .map(BestillingProgress::isPdlSync)
                                                .flatMap(isPresent -> isTrue(isPresent) ?
                                                        Flux.merge(pensjonforvalterClient.gjenopprett(new RsDollyUtvidetBestilling(), dollyperson, progress, false),
                                                                tpsMessagingClient.gjenopprett(new RsDollyUtvidetBestilling(), dollyperson, progress, false)) :
                                                        Flux.empty())
                                                .filter(Objects::nonNull)
                                                .map(ClientFuture::get)
                                                .collectList()
                                                .map(status -> RsOrdreStatus.builder()
                                                        .status(Stream.of(getStatus(BestillingPdlDataStatusMapper.buildPdlDataStatusMap(List.of(progress), objectMapper)),
                                                                        getStatus(BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap(List.of(progress))),
                                                                        getStatus(BestillingPensjonforvalterStatusMapper.buildPensjonforvalterStatusMap(List.of(progress))))
                                                                .filter(Objects::nonNull)
                                                                .toList())
                                                        .build()))))
                .blockFirst();
    }

    private Flux<BestillingProgress> sendOrdre(DollyPerson dollyPerson, BestillingProgress progress) {

        return pdlDataConsumer.sendOrdre(dollyPerson.getIdent(), false)
                .map(response -> {
                    progress.setPdlDataStatus(response.getStatus().is2xxSuccessful() ?
                            response.getJsonNode() : response.getFeilmelding());
                    log.info("Pdl-forvalter status: {}", progress.getPdlDataStatus());
                    return progress;
                });
    }
}
