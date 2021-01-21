package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aktoerregister.AktoerregisterClient;
import no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.IdentRepository.GruppeBestillingIdent;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TpsfPersonCache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Service
public class GjenopprettGruppeService extends DollyBestillingService {

    private BestillingService bestillingService;
    private ErrorStatusDecoder errorStatusDecoder;
    private TpsfService tpsfService;
    private ForkJoinPool dollyForkJoinPool;
    private TpsfPersonCache tpsfPersonCache;
    private List<ClientRegister> clientRegisters;
    private IdentService identService;

    public GjenopprettGruppeService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService, TpsfPersonCache tpsfPersonCache,
                                    IdentService identService, BestillingProgressService bestillingProgressService,
                                    BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
                                    ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                    ErrorStatusDecoder errorStatusDecoder, ForkJoinPool dollyForkJoinPool) {
        super(tpsfResponseHandler, tpsfService, tpsfPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.tpsfService = tpsfService;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.tpsfPersonCache = tpsfPersonCache;
        this.clientRegisters = clientRegisters;
        this.identService = identService;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            List<GruppeBestillingIdent> coBestillinger = identService.getBestillingerFromGruppe(bestilling.getGruppe());

            dollyForkJoinPool.submit(() -> {
                bestilling.getGruppe().getTestidenter().parallelStream()
                        .filter(testident -> !bestillingService.isStoppet(bestilling.getId()))
                        .map(testident -> {

                            BestillingProgress progress = new BestillingProgress(bestilling.getId(), testident.getIdent());
                            try {
                                List<Person> personer = tpsfService.hentTestpersoner(List.of(testident.getIdent()));

                                if (!personer.isEmpty()) {
                                    TpsPerson tpsPerson = tpsfPersonCache.prepareTpsPersoner(personer.get(0));
                                    sendIdenterTilTPS(Stream.of(bestilling.getMiljoer().split(",")).collect(Collectors.toList()),
                                            Stream.of(List.of(tpsPerson.getHovedperson()), tpsPerson.getPartnere(), tpsPerson.getBarn())
                                                    .flatMap(Collection::stream)
                                                    .collect(toList()),
                                            bestilling.getGruppe(), progress);

                                    gjenopprettNonTpsf(tpsPerson, bestKriterier, progress, false);

                                    coBestillinger.stream()
                                            .filter(gruppe -> gruppe.getIdent().equals(testident.getIdent()))
                                            .sorted(Comparator.comparing(GruppeBestillingIdent::getBestillingid))
                                            .map(bestilling1 -> clientRegisters.stream()
                                                    .filter(register ->
                                                            !(register instanceof PdlForvalterClient ||
                                                                    register instanceof AktoerregisterClient ||
                                                                    register instanceof PensjonforvalterClient))
                                                    .map(register -> {
                                                        register.gjenopprett(getDollyBestillingRequest(
                                                                Bestilling.builder()
                                                                        .bestKriterier(bestilling1.getBestkriterier())
                                                                        .miljoer(bestilling.getMiljoer())
                                                                        .build()), tpsPerson, progress, false);
                                                        return register;
                                                    })
                                                    .collect(toList()))
                                            .collect(toList());

                                } else {
                                    progress.setFeil("NA:Feil= Finner ikke personen i database");
                                }

                            } catch (RuntimeException e) {
                                progress.setFeil(errorStatusDecoder.decodeRuntimeException(e));

                            } finally {
                                oppdaterProgress(bestilling, progress);
                            }
                            return null;
                        })
                        .collect(toList());
                oppdaterBestillingFerdig(bestilling);
            });
        }
    }

    @Data
    @Builder
    public static class BestKriterier {

        private Long bestillingId;
        private String bestKristerier;
    }
}
