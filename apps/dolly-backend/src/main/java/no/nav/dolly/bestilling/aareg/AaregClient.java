package no.nav.dolly.bestilling.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.amelding.AmeldingService;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.YearMonth;
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
import static no.nav.dolly.util.EnvironmentsCrossConnect.Type.Q4_TO_Q1;
import static no.nav.dolly.util.EnvironmentsCrossConnect.crossConnect;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class AaregClient implements ClientRegister {

    public static final String IDENT = "Ident";
    private static final String SYSTEM = "AAREG";

    private final AaregConsumer aaregConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final AmeldingService ameldingService;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getAareg().isEmpty()) {

            var miljoer = crossConnect(bestilling.getEnvironments(), Q4_TO_Q1);
            log.info("dollyPerson: {}", dollyPerson, new NullPointerException("Stack trace"));
            if (dollyPerson.getBruker() != null) {
                log.info("dollyPerson.getBruker(): {}", dollyPerson.getBruker());
                log.info("dollyPerson.getBruker().getBrukertype(): {}", dollyPerson.getBruker().getBrukertype());
                if (dollyPerson.getBruker().getBrukertype() == Bruker.Brukertype.BANKID) {
                    miljoer = crossConnect(miljoer, Q1_AND_Q2);
                }
            }
            var miljoerTrygg = new AtomicReference<>(miljoer);

            var initStatus = miljoer.stream()
                    .map(miljo -> String.format("%s:%s", miljo, getInfoVenter(SYSTEM)))
                    .collect(Collectors.joining(","));
            transactionHelperService.persister(progress, BestillingProgress::setAaregStatus, initStatus);

            return Flux.just(1)
                    .flatMap(index -> {
                        if (bestilling.getAareg().stream()
                                .map(RsAareg::getAmelding)
                                .allMatch(List::isEmpty)) {

                            return sendArbeidsforhold(bestilling, dollyPerson, miljoerTrygg.get(), isOpprettEndre);
                        } else {
                            return ameldingService.sendAmelding(bestilling, dollyPerson, miljoerTrygg.get());
                        }
                    })
                    .map(status -> futurePersist(progress, status));
        }
        return Flux.empty();
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setAaregStatus, status);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        // Sletting av arbeidsforhold er pt ikke støttet
    }

    private Mono<String> sendArbeidsforhold(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                            Set<String> miljoer, boolean isOpprettEndre) {

        MappingContext context = new MappingContext.Factory().getContext();
        context.setProperty(IDENT, dollyPerson.getIdent());
        var arbeidsforholdRequest = mapperFacade.mapAsList(bestilling.getAareg(), Arbeidsforhold.class, context);

        return aaregConsumer.getAccessToken()
                .flatMapMany(token -> Flux.fromIterable(miljoer)
                        .parallel()
                        .flatMap(miljoe -> aaregConsumer.hentArbeidsforhold(dollyPerson.getIdent(), miljoe, token)
                                .flatMapMany(response -> doInsertOrUpdate(response, arbeidsforholdRequest, miljoe, token, isOpprettEndre))))
                .collect(Collectors.joining(","));
    }

    private Flux<String> doInsertOrUpdate(ArbeidsforholdRespons response, List<Arbeidsforhold> request,
                                          String miljoe, AccessToken token, boolean isOpprettEndre) {

        var arbforholdId = new AtomicInteger(response.getEksisterendeArbeidsforhold().size());

        var eksistens = doEksistenssjekk(response, mapperFacade.mapAsList(request, Arbeidsforhold.class), isOpprettEndre);
        return Flux.merge(Flux.fromIterable(eksistens.getNyeArbeidsforhold())
                                .flatMap(entry -> {
                                    if (isBlank(entry.getArbeidsforholdId())) {
                                        entry.setArbeidsforholdId(Integer.toString(arbforholdId.incrementAndGet()));
                                    }
                                    appendPermisjonPermitteringId(entry, null);
                                    return aaregConsumer.opprettArbeidsforhold(entry, miljoe, token);
                                }),
                        Flux.fromIterable(eksistens.getEksisterendeArbeidsforhold())
                                .flatMap(eksisterende -> appendArbeidsforholdId(response, eksisterende)
                                        .flatMap(arbeidsforhold -> aaregConsumer.endreArbeidsforhold(arbeidsforhold, miljoe, token))))
                .map(reply -> decodeStatus(miljoe, reply));
    }

    private Flux<Arbeidsforhold> appendArbeidsforholdId(ArbeidsforholdRespons response, Arbeidsforhold arbeidsforhold) {

        response.getEksisterendeArbeidsforhold()
                .forEach(eksisterende -> {
                    if (isEqualArbeidsforhold(eksisterende, arbeidsforhold)) {
                        arbeidsforhold.setArbeidsforholdId(isNotBlank(arbeidsforhold.getArbeidsforholdId()) ?
                                arbeidsforhold.getArbeidsforholdId() : eksisterende.getArbeidsforholdId());
                        arbeidsforhold.setNavArbeidsforholdId(eksisterende.getNavArbeidsforholdId());
                        arbeidsforhold.setNavArbeidsforholdPeriode(nonNull(arbeidsforhold.getNavArbeidsforholdPeriode()) ?
                                arbeidsforhold.getNavArbeidsforholdPeriode() : YearMonth.now());
                        appendPermisjonPermitteringId(arbeidsforhold, eksisterende);
                    }
                });

        return Flux.just(arbeidsforhold);
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
