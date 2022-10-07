package no.nav.dolly.bestilling.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.amelding.AmeldingService;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.AaregResponse;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.bestilling.aareg.util.AaregUtil;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.EnvironmentsCrossConnect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.aareg.util.AaregUtil.appendResult;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarsel;

@Slf4j
@Order(6)
@Service
@RequiredArgsConstructor
public class AaregClient implements ClientRegister {

    private final AaregConsumer aaregConsumer;
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
            miljoer.forEach(env -> {
                if (!bestilling.getAareg().get(0).getAmelding().isEmpty()) {
                    ameldingService.sendAmelding(bestilling, dollyPerson, progress, result, env);
                } else {
                    sendArbeidsforhold(bestilling, dollyPerson, isOpprettEndre, result, env);
                }
            });
        }

        progress.setAaregStatus(result.length() > 1 ? result.substring(1) : null);
    }

    @Override
    public void release(List<String> identer) {

        try {
            aaregConsumer.slettArbeidsforholdFraAlleMiljoer(identer)
                    .subscribe(response -> {
                        var status = response.stream()
                                .map(AaregResponse::getStatusPerMiljoe)
                                .map(Map::values)
                                .flatMap(Collection::stream)
                                .distinct()
                                .map(string -> string.replace("\n", ""))
                                .toList();
                        if (status.isEmpty()) {
                            log.info("Sletting mot Aareg utført");
                        } else {
                            log.info("Sletting mot Aareg utført: {}", String.join("\n", status));
                        }
                    });

        } catch (RuntimeException e) {
            log.error("Slettet fra aareg feilet: " + String.join(", ", identer));
        }
    }

    private void sendArbeidsforhold(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                    boolean isOpprettEndre, StringBuilder result, String env) {
        try {

            MappingContext context = new MappingContext.Factory().getContext();

            List<Arbeidsforhold> arbeidsforholdRequest =
                    nonNull(bestilling.getAareg().get(0)) ? mapperFacade.mapAsList(bestilling.getAareg(), Arbeidsforhold.class, context) : emptyList();
            List<ArbeidsforholdResponse> eksisterendeArbeidsforhold = aaregConsumer.hentArbeidsforhold(dollyPerson.getHovedperson(), env);

            List<Arbeidsforhold> arbeidsforhold = AaregUtil.merge(
                    arbeidsforholdRequest,
                    eksisterendeArbeidsforhold,
                    dollyPerson.getHovedperson(), isOpprettEndre);

            arbeidsforhold.forEach(arbforhold -> {
                AaregOpprettRequest aaregOpprettRequest = AaregOpprettRequest.builder()
                        .arbeidsforhold(arbforhold)
                        .environments(singletonList(env))
                        .build();
                aaregConsumer.opprettArbeidsforhold(aaregOpprettRequest).getStatusPerMiljoe().entrySet().forEach(entry ->
                        appendResult(entry, arbforhold.getArbeidsforholdID(), result));
            });

            if (arbeidsforhold.isEmpty()) {
                appendResult(Map.entry(env, "OK"), "0", result);
            }
        } catch (RuntimeException e) {
            log.error("Innsending til Aareg feilet: ", e);
            appendResult(Map.entry(env, errorStatusDecoder.decodeRuntimeException(e)), "1", result);
        }
    }

}