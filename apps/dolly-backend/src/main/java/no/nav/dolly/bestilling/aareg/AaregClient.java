package no.nav.dolly.bestilling.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.aareg.util.AaregUtility.appendPermisjonPermitteringId;
import static no.nav.dolly.bestilling.aareg.util.AaregUtility.doEksistenssjekk;
import static no.nav.dolly.bestilling.aareg.util.AaregUtility.isEqualArbeidsforhold;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.util.EnvironmentsCrossConnect.Type.Q1_AND_Q2;
import static no.nav.dolly.util.EnvironmentsCrossConnect.crossConnect;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class AaregClient implements ClientRegister {

    public static final Set<String> MILJOER_SUPPORTED = Set.of("q1", "q2", "q4");
    public static final String IDENT = "Ident";
    private static final String SYSTEM = "AAREG";

    private final AaregConsumer aaregConsumer;
    private final ApplicationConfig applicationConfig;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getAareg().isEmpty() &&
                bestilling.getAareg().stream().anyMatch(aareg -> nonNull(aareg.getArbeidsgiver()))) {

            var miljoer = bestilling.getEnvironments().stream()
                    .filter(MILJOER_SUPPORTED::contains)
                    .collect(Collectors.toSet());

            if (dollyPerson.getBruker().getBrukertype() == Bruker.Brukertype.BANKID) {
                miljoer = crossConnect(miljoer, Q1_AND_Q2);
            }
            var miljoerTrygg = new AtomicReference<>(miljoer);

            var initStatus = miljoer.stream()
                    .map(miljo -> "%s:%s".formatted(miljo, getInfoVenter(SYSTEM)))
                    .collect(Collectors.joining(","));

            transactionHelperService.persister(progress, BestillingProgress::getAaregStatus,
                    BestillingProgress::setAaregStatus, initStatus);

            return Flux.from(sendArbeidsforhold(bestilling, dollyPerson, miljoerTrygg.get(), isOpprettEndre)
                    .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                    .onErrorResume(error -> getErrors(error, miljoerTrygg.get()))
                    .map(status -> futurePersist(progress, status)));
        }
        return Flux.empty();
    }

    private Mono<String> getErrors(Throwable error, Set<String> miljoer) {

        var decoded = WebClientError.describe(error);
        return Mono.just(miljoer.stream()
                .map(miljoe -> "%s:Feil= %s".formatted(miljoe, ErrorStatusDecoder.encodeStatus(decoded.getStatus() +
                        (isBlank(decoded.getMessage()) ? "" : "= %s".formatted(decoded.getMessage())))))
                .collect(Collectors.joining(",")));
    }

    @Override
    public void release(List<String> identer) {

        // Sletting av arbeidsforhold er pt ikke støttet
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::getAaregStatus,
                    BestillingProgress::setAaregStatus, status);
            return progress;
        };
    }

    private Mono<String> sendArbeidsforhold(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                            Set<String> miljoer, boolean isOpprettEndre) {

        var context = MappingContextUtils.getMappingContext();
        context.setProperty(IDENT, dollyPerson.getIdent());

        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> aaregConsumer.hentArbeidsforhold(dollyPerson.getIdent(), miljoe))
                .collect(Collectors.toMap(ArbeidsforholdRespons::getMiljo, ArbeidsforholdRespons::getEksisterendeArbeidsforhold))
                .doOnNext(eksisterendeArbeidsforhold -> updateBestilling(bestilling, eksisterendeArbeidsforhold.values()))
                .flatMap(eksisterende -> Flux.fromIterable(bestilling.getAareg())
                        .filter(aareg -> nonNull(aareg.getArbeidsgiver()))
                        .map(aareg -> mapperFacade.map(aareg, Arbeidsforhold.class, context))
                        .collectList()
                        .flatMap(arbeidsforholdRequest -> Flux.fromIterable(miljoer)
                                .flatMap(miljoe -> doInsertOrUpdate(arbeidsforholdRequest, eksisterende.get(miljoe), miljoe, isOpprettEndre))
                                .collect(Collectors.joining(","))));
    }

    private void updateBestilling(RsDollyUtvidetBestilling bestilling, Collection<List<Arbeidsforhold>> eksisterendeArbeidsforhold) {

        var antallArbeidsforhold = new AtomicInteger(eksisterendeArbeidsforhold.stream()
                .map(List::size)
                .max(Comparator.comparing(Integer::intValue))
                .orElse(null));

        if (bestilling.getAareg().stream()
                .anyMatch(aareg -> nonNull(aareg.getArbeidsgiver()) && isBlank(aareg.getArbeidsforholdId()))) {

            bestilling.getAareg().stream()
                    .filter(aareg -> nonNull(aareg.getArbeidsgiver()) &&
                            isBlank(aareg.getArbeidsforholdId()))
                    .forEach(aareg ->
                            aareg.setArbeidsforholdId(Integer.toString(antallArbeidsforhold.incrementAndGet())));

            transactionHelperService.persister(bestilling.getId(), bestilling);
        }
    }

    private Flux<String> doInsertOrUpdate(List<Arbeidsforhold> request, List<Arbeidsforhold> eksisterende,
                                          String miljoe, boolean isOpprettEndre) {

        var eksistens = doEksistenssjekk(mapperFacade.mapAsList(request, Arbeidsforhold.class), eksisterende, isOpprettEndre);
        return Flux.merge(Flux.fromIterable(eksistens.getNyeArbeidsforhold())
                                .flatMap(entry -> {
                                    appendPermisjonPermitteringId(entry, null);
                                    return aaregConsumer.opprettArbeidsforhold(entry, miljoe);
                                }),
                        Flux.fromIterable(eksistens.getEksisterendeArbeidsforhold())
                                .doOnNext(arbeidsforhold -> appendArbeidsforholdId(arbeidsforhold, eksisterende))
                                .flatMap(arbeidsforhold -> aaregConsumer.endreArbeidsforhold(arbeidsforhold, miljoe)))
                .map(reply -> decodeStatus(miljoe, reply));
    }

    private void appendArbeidsforholdId(Arbeidsforhold arbeidsforhold, List<Arbeidsforhold> eksisterende) {

        eksisterende
                .forEach(eksisterende1 -> {
                    if (isEqualArbeidsforhold(eksisterende1, arbeidsforhold)) {
                        arbeidsforhold.setNavArbeidsforholdId(eksisterende1.getNavArbeidsforholdId());
                        arbeidsforhold.setNavArbeidsforholdPeriode(nonNull(arbeidsforhold.getNavArbeidsforholdPeriode()) ?
                                arbeidsforhold.getNavArbeidsforholdPeriode() : YearMonth.now());
                        appendPermisjonPermitteringId(arbeidsforhold, eksisterende1);
                    }
                });
    }

    private String decodeStatus(String miljoe, ArbeidsforholdRespons reply) {
        log.info("AAREG respons fra miljø {} : {} ", miljoe, reply);
        return "%s: arbforhold=%s$%s".formatted(
                miljoe,
                reply.getArbeidsforholdId(),
                isNull(reply.getError()) ? "OK" : errorStatusDecoder.decodeThrowable(reply.getError())
        );
    }
}
