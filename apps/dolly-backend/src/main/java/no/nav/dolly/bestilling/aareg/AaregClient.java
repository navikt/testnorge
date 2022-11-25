package no.nav.dolly.bestilling.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.amelding.AmeldingService;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.bestilling.service.DoneService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.EnvironmentsCrossConnect;
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
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselSlutt;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselVenter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Order(6)
@Service
@RequiredArgsConstructor
public class AaregClient implements ClientRegister {

    public static final String IDENT = "Ident";

    private final AaregConsumer aaregConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final AmeldingService ameldingService;
    private final DoneService doneService;

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getAareg().isEmpty()) {

            var miljoer = EnvironmentsCrossConnect.crossConnect(bestilling.getEnvironments());

            progress.setAaregStatus(miljoer.stream()
                    .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getVarselVenter("AAREG"))))
                    .collect(Collectors.joining(",")));
            doneService.persist(progress);

            var isAvail = false;
            var loops = DoneService.MAX_AWAIT_CYCLES;
            while (loops-- > 0 && !(isAvail = doneService.isPdlSync(dollyPerson.getHovedperson()))) ;

            if (isAvail) {
                (bestilling.getAareg().stream()
                        .map(RsAareg::getAmelding)
                        .anyMatch(amelding -> !amelding.isEmpty()) ?

                        ameldingService.sendAmelding(bestilling, dollyPerson, miljoer) :
                        sendArbeidsforhold(bestilling, dollyPerson, miljoer)
                ).subscribe(response -> {
                    progress.setAaregStatus(response);
                    doneService.isDone(progress);
                });

            } else {
                progress.setAaregStatus(miljoer.stream()
                        .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getVarselSlutt("AAREG"))))
                        .collect(Collectors.joining(",")));
                doneService.isDone(progress);
            }
        }
        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

        // Sletting av arbeidsforhold er pt ikke støttet
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getAareg()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getAaregStatus()) &&
                                !entry.getAaregStatus().contains("Info: Venter"));
    }

    private Mono<String> sendArbeidsforhold(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, List<String> miljoer) {

        MappingContext context = new MappingContext.Factory().getContext();
        context.setProperty(IDENT, dollyPerson.getHovedperson());
        var arbeidsforholdRequest = mapperFacade.mapAsList(bestilling.getAareg(), Arbeidsforhold.class, context);

        return aaregConsumer.getAccessToken()
                .flatMapMany(token -> Flux.fromIterable(miljoer)
                        .parallel()
                        .flatMap(miljoe -> aaregConsumer.hentArbeidsforhold(dollyPerson.getHovedperson(), miljoe, token)
                                .flatMapMany(response -> doInsertOrUpdate(response, arbeidsforholdRequest, miljoe, token))))
                .collect(Collectors.joining(","));
    }

    private Flux<String> doInsertOrUpdate(ArbeidsforholdRespons response, List<Arbeidsforhold> request,
                                          String miljoe, AccessToken token) {

        var arbforholdId = new AtomicInteger(response.getEksisterendeArbeidsforhold().size());

        var eksistens = doEksistenssjekk(response, mapperFacade.mapAsList(request, Arbeidsforhold.class));
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
