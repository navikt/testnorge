package no.nav.dolly.bestilling.aaregrest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.amelding.AmeldingService;
import no.nav.dolly.bestilling.aaregrest.domain.ArbeidsforholdEksistens;
import no.nav.dolly.bestilling.aaregrest.domain.ArbeidsforholdRespons;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.EnvironmentsCrossConnect;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.aaregrest.util.AaaregUtility.isEqualArbeidsforhold;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarsel;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Order(6)
@Service
@RequiredArgsConstructor
public class Aareg2Client implements ClientRegister {

    public static final String IDENT = "Ident";

    private final Aareg2Consumer aaregConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final AmeldingService ameldingService;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        StringBuilder result = new StringBuilder();

        if (!bestilling.getAareg().isEmpty()) {

            if (!dollyPerson.isOpprettetIPDL()) {
                progress.setAaregStatus(bestilling.getEnvironments().stream()
                        .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getVarsel("AAREG"))))
                        .collect(Collectors.joining(",")));
                return;
            }

            var miljoer = EnvironmentsCrossConnect.crossConnect(bestilling.getEnvironments());

            if (bestilling.getAareg().stream()
                    .map(RsAareg::getAmelding)
                    .anyMatch(amelding -> !amelding.isEmpty())) {

                miljoer.forEach(env ->
                        ameldingService.sendAmelding(bestilling, dollyPerson, progress, result, env));

            } else {

                result.append(sendArbeidsforhold(bestilling, dollyPerson, isOpprettEndre, miljoer));
            }
        }

        progress.setAaregStatus(result.length() > 1 ? result.substring(1) : null);
    }

    @Override
    public void release(List<String> identer) {

        // Sletting av arbeidsforhold er pt ikke støttet
    }

    private String sendArbeidsforhold(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                      boolean isOpprettEndre, List<String> miljoer) {


        MappingContext context = new MappingContext.Factory().getContext();
        context.setProperty(IDENT, dollyPerson.getHovedperson());
        var arbeidsforholdRequest = mapperFacade.mapAsList(bestilling.getAareg(), Arbeidsforhold.class, context);

        return StringUtils.join(
                aaregConsumer.getAccessToken()
                        .flatMapMany(token -> Flux.fromIterable(miljoer)
                                .flatMap(miljoe -> aaregConsumer.hentArbeidsforhold(dollyPerson.getHovedperson(), miljoe, token)
                                        .flatMapMany(response -> doInsertOrUpdate(response, arbeidsforholdRequest, miljoe, token))))
                        .collectList()
                        .block(), ",");
    }

    private Flux<String> doInsertOrUpdate(ArbeidsforholdRespons response, List<Arbeidsforhold> request,
                                          String miljoe, AccessToken token) {


        var arbforholdId = new AtomicInteger(0);

        if (isNull(response.getError()) || response.getError() instanceof WebClientResponseException.NotFound) {

            return Flux.fromIterable(request)
                    .flatMap(entry -> {
                        if (isBlank(entry.getArbeidsforholdId())) {
                            entry.setArbeidsforholdId(Integer.toString(arbforholdId.incrementAndGet()));
                        }
                        return aaregConsumer.opprettArbeidsforhold(entry, miljoe, token)
                                .map(reply -> decodeStatus(miljoe, reply));
                    });
        } else {

            var eksistens = doEksistenssjekk(response, request);
            return Flux.concat(Flux.fromIterable(eksistens.getNyeArbeidsforhold())
                    .flatMap(entry -> {
                        if (isBlank(entry.getArbeidsforholdId())) {
                            entry.setArbeidsforholdId(Integer.toString(arbforholdId.incrementAndGet()));
                        }
                        return aaregConsumer.opprettArbeidsforhold(entry, miljoe, token);
                    }),
                    Flux.fromIterable(eksistens.getEksisterendeArbeidsforhold())
                            .flatMap(entry -> aaregConsumer.endreArbeidsforhold(entry, miljoe, token)))
                    .map(reply -> decodeStatus(miljoe, reply));
        }
    }

    private ArbeidsforholdEksistens doEksistenssjekk(ArbeidsforholdRespons response, List<Arbeidsforhold> request) {

        return ArbeidsforholdEksistens.builder()
                .nyeArbeidsforhold(request.stream()
                        .filter(arbeidsforhold -> response.getEksisterendeArbeidsforhold().stream()
                                .anyMatch(response1 -> isEqualArbeidsforhold(response1, arbeidsforhold)))
                        .map(arbeidsforhold -> appendArbeidsforholdId(response, arbeidsforhold))
                        .toList())
                .eksisterendeArbeidsforhold(request.stream()
                        .filter(arbeidsforhold -> response.getEksisterendeArbeidsforhold().stream()
                                .anyMatch(response1 -> !isEqualArbeidsforhold(response1, arbeidsforhold)))
                        .map(arbeidsforhold -> appendArbeidsforholdId(response, arbeidsforhold))
                        .toList())
                .build();
    }

    private Arbeidsforhold appendArbeidsforholdId(ArbeidsforholdRespons response, Arbeidsforhold arbeidsforhold) {

        response.getEksisterendeArbeidsforhold()
                .forEach(ekisterende -> {
                    if (isEqualArbeidsforhold(ekisterende, arbeidsforhold)) {
                        arbeidsforhold.setArbeidsforholdId(isNotBlank(arbeidsforhold.getArbeidsforholdId()) ?
                                arbeidsforhold.getArbeidsforholdId() : ekisterende.getArbeidsforholdId());
                        arbeidsforhold.setNavArbeidsforholdId(ekisterende.getNavArbeidsforholdId());
                        arbeidsforhold.setNavArbeidsforholdPeriode(nonNull(arbeidsforhold.getNavArbeidsforholdPeriode()) ?
                                arbeidsforhold.getNavArbeidsforholdPeriode() : YearMonth.now());
                    }
                });
        return arbeidsforhold;
    }

    private String decodeStatus(String miljoe, ArbeidsforholdRespons reply) {

        StringBuilder builder = new StringBuilder();
        builder.append(miljoe)
                .append(": arbforhold=")
                .append(reply.getArbeidsforhold().getArbeidsforholdId())
                .append('$');

        if (isNull(reply.getError())) {
            builder.append("OK;");

        } else if (reply.getError() instanceof RuntimeException runtimeException) {
            builder.append(errorStatusDecoder.decodeRuntimeException(runtimeException));

        } else if (reply.getError() instanceof Exception exception) {
            builder.append(errorStatusDecoder.decodeException(exception));

        } else {
            builder.append(reply.getError().getMessage());
            builder.append(';');
            log.error(reply.getError().getMessage(), reply.getError());
        }
        return builder.toString();
    }
}

