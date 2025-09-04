package no.nav.dolly.bestilling.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.aareg.util.AaregUtility.appendArbeidsforholdId;
import static no.nav.dolly.bestilling.aareg.util.AaregUtility.getMaxArbeidsforholdId;
import static no.nav.dolly.bestilling.aareg.util.AaregUtility.getMaxPermisjonPermitteringId;
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
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (bestilling.getAareg().isEmpty() ||
                bestilling.getAareg().stream().noneMatch(aareg -> nonNull(aareg.getArbeidsgiver()))) {

            return Mono.empty();
        }

        var miljoer = bestilling.getEnvironments().stream()
                .filter(MILJOER_SUPPORTED::contains)
                .collect(Collectors.toSet());

        if (dollyPerson.getBruker().getBrukertype() == Bruker.Brukertype.BANKID) {
            miljoer = crossConnect(miljoer, Q1_AND_Q2);
        }
        var miljoerTrygg = new AtomicReference<>(miljoer);

        return oppdaterStatus(progress, miljoer.stream()
                .map(miljo -> "%s:%s".formatted(miljo, getInfoVenter(SYSTEM)))
                .collect(Collectors.joining(",")))
                .then(sendArbeidsforhold(bestilling, dollyPerson, miljoerTrygg.get(), isOpprettEndre)
                        .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                        .onErrorResume(error -> getErrors(error, miljoerTrygg.get()))
                        .flatMap(status -> oppdaterStatus(progress, status)));
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

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::getAaregStatus,
                BestillingProgress::setAaregStatus, status);
    }

    private Mono<String> sendArbeidsforhold(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                            Set<String> miljoer, boolean isOpprettEndre) {

        var context = MappingContextUtils.getMappingContext();
        context.setProperty(IDENT, dollyPerson.getIdent());

        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> aaregConsumer.hentArbeidsforhold(dollyPerson.getIdent(), miljoe))
                .flatMap(eksisterende -> Flux.fromIterable(bestilling.getAareg())
                        .filter(aareg -> nonNull(aareg.getArbeidsgiver()))
                        .map(aareg -> mapperFacade.map(aareg, Arbeidsforhold.class, context))
                        .collectList()
                        .flatMapMany(arbeidsforholdRequest -> doInsertOrUpdate(arbeidsforholdRequest, eksisterende, isOpprettEndre)))
                        .collect(Collectors.joining(","));
    }

    private Flux<String> doInsertOrUpdate(List<Arbeidsforhold> request, ArbeidsforholdRespons eksisterendeArbeidsforhold,
                                          boolean isOpprettEndre) {

        var miljoe = eksisterendeArbeidsforhold.getMiljoe();
        var eksisterende = eksisterendeArbeidsforhold.getEksisterendeArbeidsforhold();
        var antallArbeidsforhold = new AtomicInteger(Math.max(
                getMaxArbeidsforholdId(request),
                getMaxArbeidsforholdId(eksisterende)));
        var antallPermisjonPermittering = new AtomicInteger(Math.max(
                getMaxPermisjonPermitteringId(request),
                getMaxPermisjonPermitteringId(eksisterende)));

        if (isOpprettEndre) {
            return Flux.fromIterable(request)
                    .map(arbeidsforhold ->
                        appendArbeidsforholdId(arbeidsforhold, true, eksisterende, antallArbeidsforhold, antallPermisjonPermittering))
                    .flatMap(arbeidsforhold -> aaregConsumer.opprettArbeidsforhold(arbeidsforhold, miljoe))
                    .map(reply -> decodeStatus(miljoe, reply));
        } else {

            return Flux.fromIterable(request)
                    .flatMap(arbeidsforhold -> {
                        if (eksisterende.stream().anyMatch(eksisterende1 -> isEqualArbeidsforhold(eksisterende1, arbeidsforhold))) {
                            appendArbeidsforholdId(arbeidsforhold, false, eksisterende, antallArbeidsforhold, antallPermisjonPermittering);
                            return aaregConsumer.endreArbeidsforhold(arbeidsforhold, miljoe)
                                    .map(reply -> decodeStatus(miljoe, reply));
                        } else {
                            appendArbeidsforholdId(arbeidsforhold, true, eksisterende, antallArbeidsforhold, antallPermisjonPermittering);
                            return aaregConsumer.opprettArbeidsforhold(arbeidsforhold, miljoe)
                            .map(reply -> decodeStatus(miljoe, reply));
                        }
                    });
        }
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
