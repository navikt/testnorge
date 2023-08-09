package no.nav.dolly.bestilling.arenaforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.arenaforvalter.service.ArenaAap115Service;
import no.nav.dolly.bestilling.arenaforvalter.service.ArenaAapService;
import no.nav.dolly.bestilling.arenaforvalter.service.ArenaBrukerService;
import no.nav.dolly.bestilling.arenaforvalter.service.ArenaDagpengerService;
import no.nav.dolly.bestilling.arenaforvalter.service.ArenaStansYtelseService;
import no.nav.dolly.bestilling.arenaforvalter.utils.ArenaEksisterendeVedtakUtil;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.util.TransactionHelperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.AAP;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.AAP115;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.BRUKER;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.DAGPENGER;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.fmtResponse;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaForvalterClient implements ClientRegister {

    private static final String MILJOE_FMT = "%s$BRUKER= %s";
    private static final String SYSTEM = "Arena";

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final TransactionHelperService transactionHelperService;
    private final ArenaBrukerService arenaBrukerService;
    private final ArenaAap115Service arenaAap115Service;
    private final ArenaAapService arenaAapService;
    private final ArenaDagpengerService arenaDagpengerService;
    private final ArenaStansYtelseService arenaStansYtelseService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.just(bestilling)
                .filter(best -> nonNull(best.getArenaforvalter()))
                .map(RsDollyUtvidetBestilling::getArenaforvalter)
                .flatMap(ordre -> arenaForvalterConsumer.getEnvironments()
                        .filter(env -> bestilling.getEnvironments().contains(env))
                        .collectList()
                        .doOnNext(miljoer -> {
                            var initStatus = miljoer.stream()
                                    .map(miljo -> String.format(MILJOE_FMT, miljo, getInfoVenter(SYSTEM)))
                                    .collect(Collectors.joining(","));
                            transactionHelperService.persister(progress, BestillingProgress::setArenaforvalterStatus, initStatus);
                        })
                        .flatMap(miljoer -> doArenaOpprett(ordre, dollyPerson.getIdent(), miljoer)
                                .map(status -> futurePersist(progress, status))));
    }

    private Mono<String> doArenaOpprett(Arenadata arenadata, String ident, List<String> miljoer) {

        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> arenaForvalterConsumer.getArenaBruker(ident, miljoe)
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
                        )))
                .collect(Collectors.joining(","));
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setArenaforvalterStatus, StringUtils.left(status, 4000));
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        arenaForvalterConsumer.deleteIdenter(identer)
                .collectList()
                .subscribe(response -> log.info("Sletting utført mot Arena-forvalteren"));
    }
}
