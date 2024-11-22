package no.nav.dolly.bestilling.pensjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pensjonforvalter.service.PensjonPdlPersonService;
import no.nav.dolly.bestilling.pensjonforvalter.service.PensjonPensjonsdataService;
import no.nav.dolly.bestilling.pensjonforvalter.service.PensjonPersondataService;
import no.nav.dolly.bestilling.pensjonforvalter.service.PensjonVedtakService;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.IdentType;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.IdentTypeUtil;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.ANNET;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PENSJON_FORVALTER;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.SEP;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.SYSTEM;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonforvalterClient implements ClientRegister {

    private final ApplicationConfig applicationConfig;
    private final PensjonPdlPersonService pensjonPdlPersonService;
    private final PensjonPensjonsdataService pensjonPensjonsdataService;
    private final PensjonPersondataService pensjonPersondataService;
    private final PensjonVedtakService pensjonVedtakService;
    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (IdentTypeUtil.getIdentType(dollyPerson.getIdent()) == IdentType.NPID) {
            return Flux.empty();
        }

        var bestilteMiljoer = new AtomicReference<>(bestilling.getEnvironments().stream()
                .map(miljoe -> miljoe.equals("q4") ? "q1" : miljoe)
                .collect(Collectors.toSet()));

        return Flux.from(Flux.from(pensjonforvalterConsumer.getMiljoer())
                .flatMap(tilgjengeligeMiljoer -> {
                    bestilteMiljoer.set(bestilteMiljoer.get().stream()
                            .filter(tilgjengeligeMiljoer::contains)
                            .collect(Collectors.toSet()));
                    return Flux.just(bestilling)
                            .doOnNext(bestilling1 -> {
                                if (!dollyPerson.isOrdre()) {
                                    transactionHelperService.persister(progress,
                                            BestillingProgress::getPensjonforvalterStatus,
                                            BestillingProgress::setPensjonforvalterStatus,
                                            prepInitStatus(tilgjengeligeMiljoer), SEP);
                                }
                            })
                            .flatMap(bestilling1 -> pensjonPdlPersonService.getUtvidetPersondata(dollyPerson.getIdent())
                                    .flatMapMany(utvidetPersondata ->
                                            Flux.concat(
                                                    pensjonPersondataService.lagrePersondata(dollyPerson.getIdent(), utvidetPersondata.getT1(), tilgjengeligeMiljoer),
                                                    pensjonPensjonsdataService.lagrePensjonsdata(bestilling1, dollyPerson.getIdent(), tilgjengeligeMiljoer),
                                                    pensjonVedtakService.lagrePensjonVedtak(bestilling1, dollyPerson.getIdent(), utvidetPersondata, tilgjengeligeMiljoer)
                                            )))

                            .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                            .onErrorResume(error -> getErrors(tilgjengeligeMiljoer, error));

                })
                .collect(Collectors.joining("$"))
                .map(status2 -> futurePersist(dollyPerson, progress, status2)));
    }

    private Flux<String> getErrors(Set<String> miljoer, Throwable throwable) {

        return Flux.fromIterable(miljoer)
                .map(miljo -> "%s%s:Feil= %s".formatted(ANNET, miljo,
                        encodeStatus(WebClientFilter.getMessage(throwable))));
    }

    @Override
    public void release(List<String> identer) {

        // Pensjonforvalter / POPP, AP, UT støtter pt ikke sletting

        pensjonforvalterConsumer.sletteTpForhold(identer);
        pensjonforvalterConsumer.slettePensjonsavtale(identer);
        pensjonforvalterConsumer.sletteAfpOffentlig(identer);
    }

    private String prepInitStatus(Set<String> miljoer) {

        return PENSJON_FORVALTER +
                miljoer.stream()
                        .map(miljo -> String.format("%s:%s", miljo, ErrorStatusDecoder.getInfoVenter(SYSTEM)))
                        .collect(Collectors.joining(","));
    }

    private ClientFuture futurePersist(DollyPerson dollyPerson, BestillingProgress progress, String status) {

        return () -> {
            if (!dollyPerson.isOrdre()) {
                transactionHelperService.persister(progress, BestillingProgress::getPensjonforvalterStatus,
                        BestillingProgress::setPensjonforvalterStatus, status, SEP);
            }
            return progress;
        };
    }
}