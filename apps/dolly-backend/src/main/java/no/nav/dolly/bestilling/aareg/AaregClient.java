package no.nav.dolly.bestilling.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.amelding.AmeldingService;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.EnvironmentsCrossConnect;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.aareg.util.AaregUtility.appendPermisjonPermitteringId;
import static no.nav.dolly.bestilling.aareg.util.AaregUtility.doEksistenssjekk;
import static no.nav.dolly.bestilling.aareg.util.AaregUtility.isEqualArbeidsforhold;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselSlutt;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Order(6)
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
    private final PersonServiceConsumer personServiceConsumer;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getAareg().isEmpty()) {

            var miljoer = EnvironmentsCrossConnect.crossConnect(bestilling.getEnvironments());

            progress.setAaregStatus(miljoer.stream()
                    .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getInfoVenter(SYSTEM))))
                    .collect(Collectors.joining(",")));
            transactionHelperService.persister(progress);

            return Flux.from(personServiceConsumer.getPdlSyncReady(dollyPerson.getHovedperson())
                    .flatMap(isPresent -> {
                                if (isTrue(isPresent)) {
                                    if (bestilling.getAareg().stream()
                                            .map(RsAareg::getAmelding)
                                            .allMatch(List::isEmpty)) {

                                        return sendArbeidsforhold(bestilling, dollyPerson, miljoer, isOpprettEndre);
                                    } else {
                                        return ameldingService.sendAmelding(bestilling, dollyPerson, miljoer);
                                    }
                                } else {
                                    return Mono.just(miljoer.stream()
                                            .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getVarselSlutt(SYSTEM))))
                                            .collect(Collectors.joining(",")));
                                }
                            }
                    )
                    .map(status -> futurePersist(progress, status)));
        }
        return Flux.empty();
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            progress.setAaregStatus(status);
            transactionHelperService.persister(progress);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        // Sletting av arbeidsforhold er pt ikke støttet
    }

    private Mono<String> sendArbeidsforhold(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                            List<String> miljoer, boolean isOpprettEndre) {

        MappingContext context = new MappingContext.Factory().getContext();
        context.setProperty(IDENT, dollyPerson.getHovedperson());
        var arbeidsforholdRequest = mapperFacade.mapAsList(bestilling.getAareg(), Arbeidsforhold.class, context);

        return aaregConsumer.getAccessToken()
                .flatMapMany(token -> Flux.fromIterable(miljoer)
                        .parallel()
                        .flatMap(miljoe -> aaregConsumer.hentArbeidsforhold(dollyPerson.getHovedperson(), miljoe, token)
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
        return new StringBuilder()
                .append(miljoe)
                .append(": arbforhold=")
                .append(reply.getArbeidsforholdId())
                .append('$')
                .append(isNull(reply.getError()) ? "OK" : errorStatusDecoder.decodeThrowable(reply.getError()))
                .toString();
    }

}
