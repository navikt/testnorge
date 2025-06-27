package no.nav.dolly.bestilling.arenaforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.arenaforvalter.service.*;
import no.nav.dolly.bestilling.arenaforvalter.utils.ArenaEksisterendeVedtakUtil;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.util.IdentTypeUtil;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.*;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaForvalterClient implements ClientRegister {

    private static final String NOT_SUPPORTED = "Avvik: Arena har sluttet å støtte ikke-syntetiske identer.";
    private static final String MILJOE_FMT = "%s$BRUKER= %s";
    private static final String SYSTEM = "Arena";

    private final ApplicationConfig applicationConfig;
    private final ArenaAap115Service arenaAap115Service;
    private final ArenaAapService arenaAapService;
    private final ArenaBrukerService arenaBrukerService;
    private final ArenaDagpengerService arenaDagpengerService;
    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final ArenaStansYtelseService arenaStansYtelseService;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Mono.just(bestilling)
                .filter(best -> nonNull(best.getArenaforvalter()))
                .map(RsDollyUtvidetBestilling::getArenaforvalter)
                .flatMap(ordre -> arenaForvalterConsumer.getEnvironments()
                        .filter(env -> bestilling.getEnvironments().contains(env))
                        .collectList()
                        .flatMap(miljoer -> oppdaterStatus(progress, miljoer.stream()
                                .map(miljo -> String.format(MILJOE_FMT, miljo, getInfoVenter(SYSTEM)))
                                .collect(Collectors.joining(",")))
                                .thenReturn(miljoer))
                        .flatMap(miljoer -> doArenaOpprett(ordre, dollyPerson.getIdent(), miljoer)
                                .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                                .onErrorResume(error -> Mono.just(fmtResponse(miljoer, ANDREFEIL, WebClientError.describe(error).getMessage())))
                                .flatMap(status -> oppdaterStatus(progress, status))));
    }

    private Mono<String> doArenaOpprett(Arenadata arenadata, String ident, List<String> miljoer) {

        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> {

                    if (IdentTypeUtil.isSyntetisk(ident)) {

                        return arenaForvalterConsumer.getArenaBruker(ident, miljoe)
                                .map(arenaArbeidsokerStatus -> ArenaEksisterendeVedtakUtil.getArenaOperasjoner(arenadata, arenaArbeidsokerStatus))
                                .flatMapMany(arenaOperasjoner -> Flux.concat(

                                        arenaBrukerService.sendBruker(arenadata, arenaOperasjoner, ident, miljoe)
                                                .map(brukerStatus -> fmtResponse(miljoe, BRUKER, brukerStatus)),

                                        arenaAap115Service.sendAap115(arenadata, arenaOperasjoner, ident, miljoe)
                                                .map(aap115tstaus -> fmtResponse(miljoe, AAP115, aap115tstaus)),

                                        arenaStansYtelseService.stopYtelse(arenaOperasjoner, ident, miljoe),

                                        arenaAapService.sendAap(arenadata, arenaOperasjoner, ident, miljoe)
                                                .map(aapStataus -> fmtResponse(miljoe, AAP, aapStataus)),

                                        arenaDagpengerService.sendDagpenger(arenadata, arenaOperasjoner, ident, miljoe)
                                                .map(dagpengerStatus -> fmtResponse(miljoe, DAGPENGER, dagpengerStatus))
                                ));
                    } else {
                        return Flux.just(fmtResponse(miljoe, BRUKER, NOT_SUPPORTED));
                    }
                })
                .collect(Collectors.joining(","));
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return
                transactionHelperService.persister(progress, BestillingProgress::getArenaforvalterStatus,
                        BestillingProgress::setArenaforvalterStatus, StringUtils.left(status, 4000));
    }

    @Override
    public void release(List<String> identer) {

        arenaForvalterConsumer.deleteIdenter(identer)
                .collectList()
                .subscribe(response -> log.info("Sletting utført mot Arena-forvalteren"));
    }
}
