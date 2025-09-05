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
import no.nav.dolly.domain.resultset.aareg.RsAareg;
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
                .then(sendArbeidsforhold(bestilling, dollyPerson, miljoerTrygg.get())
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
                                            Set<String> miljoer) {

        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> aaregConsumer.hentArbeidsforhold(dollyPerson.getIdent(), miljoe))
                .flatMap(eksisterende -> doInsertOrUpdate(dollyPerson.getIdent(), bestilling.getAareg(), eksisterende))
                .doOnNext(status ->
                        log.info("AAREG respons: {}", status))
                .map(reply -> decodeStatus(reply.getMiljoe(), reply))
                .collect(Collectors.joining(","))
                .flatMap(status -> transactionHelperService.persister(bestilling.getId(), bestilling)
                        .thenReturn(status));
    }

    private Flux<ArbeidsforholdRespons> doInsertOrUpdate(String ident, List<RsAareg> request, ArbeidsforholdRespons eksisterendeArbeidsforhold) {

        var miljoe = eksisterendeArbeidsforhold.getMiljoe();
        var eksisterende = eksisterendeArbeidsforhold.getEksisterendeArbeidsforhold();
        var context = MappingContextUtils.getMappingContext();
        context.setProperty(IDENT, ident);
        var bestilteArbeidsforhold = request.stream()
                .filter(aareg -> nonNull(aareg.getArbeidsgiver()))
                .collect(Collectors.toMap(RsAareg::hashCode, aareg -> mapperFacade.map(aareg, Arbeidsforhold.class, context)));

        var antallArbeidsforhold = new AtomicInteger(getMaxArbeidsforholdId(eksisterende));
        var antallPermisjonPermittering = new AtomicInteger(getMaxPermisjonPermitteringId(eksisterende));

        return Flux.fromIterable(request)
                .flatMap(arbeidsforhold -> {
                    if (eksisterende.stream().anyMatch(eksisterende1 -> isEqualArbeidsforhold(eksisterende1,
                            bestilteArbeidsforhold.get(arbeidsforhold.hashCode()), arbeidsforhold.getIdentifikasjon()))) {
                        appendArbeidsforholdId(bestilteArbeidsforhold.get(arbeidsforhold.hashCode()), false, eksisterende,
                                arbeidsforhold.getIdentifikasjon(), antallArbeidsforhold, antallPermisjonPermittering);
                        return aaregConsumer.endreArbeidsforhold(bestilteArbeidsforhold.get(arbeidsforhold.hashCode()), miljoe)
                                .zipWith(Mono.just(arbeidsforhold));
                    } else {
                        appendArbeidsforholdId(bestilteArbeidsforhold.get(arbeidsforhold.hashCode()), true, eksisterende,
                                arbeidsforhold.getIdentifikasjon(), antallArbeidsforhold, antallPermisjonPermittering);
                        return aaregConsumer.opprettArbeidsforhold(bestilteArbeidsforhold.get(arbeidsforhold.hashCode()), miljoe)
                                .zipWith(Mono.just(arbeidsforhold));
                    }
                })
                .map(reply -> {
                    if (isNull(reply.getT1().getError())) {
                        reply.getT2().getIdentifikasjon().put(miljoe, RsAareg.Identifikasjon.builder()
                                .arbeidsforholdId(reply.getT1().getArbeidsforhold().getArbeidsforholdId())
                                .navArbeidsforholdId(reply.getT1().getArbeidsforhold().getNavArbeidsforholdId())
                                .build());
                    }
                    return reply.getT1();
                });
    }

    private String decodeStatus(String miljoe, ArbeidsforholdRespons reply) {
        log.info("AAREG respons fra miljø {} : {} ", miljoe, reply);
        return "%s: arbforhold=%s$%s".formatted(
                miljoe,
                nonNull(reply.getArbeidsforhold()) ? reply.getArbeidsforhold().getArbeidsforholdId() : "1",
                isNull(reply.getError()) ? "OK" : errorStatusDecoder.decodeThrowable(reply.getError())
        );
    }
}
