package no.nav.dolly.bestilling.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.bestilling.aareg.util.AaregMergeUtil;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

@Slf4j
@Order(3)
@Service
@RequiredArgsConstructor
public class AaregClient implements ClientRegister {

    private final AaregConsumer aaregConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        StringBuilder result = new StringBuilder();

        if (!bestilling.getAareg().isEmpty()) {

            bestilling.getEnvironments().forEach(env -> {

                try {
                    List<Arbeidsforhold> arbeidsforholdRequest = mapperFacade.mapAsList(bestilling.getAareg(), Arbeidsforhold.class);
                    List<ArbeidsforholdResponse> eksisterendeArbeidsforhold = aaregConsumer.hentArbeidsforhold(dollyPerson.getHovedperson(), env);

                    List<Arbeidsforhold> arbeidsforhold = AaregMergeUtil.merge(
                            arbeidsforholdRequest,
                            eksisterendeArbeidsforhold,
                            dollyPerson.getHovedperson(), isOpprettEndre);

                    arbeidsforhold.forEach(arbforhold ->
                            appendResult(aaregConsumer.opprettArbeidsforhold(AaregOpprettRequest.builder()
                                    .arbeidsforhold(arbforhold)
                                    .environments(singletonList(env))
                                    .build()).getStatusPerMiljoe(), arbforhold.getArbeidsforholdID(), result));

                    if (arbeidsforhold.isEmpty()) {
                        appendResult(singletonMap(env, "OK"), "0", result);
                    }

                } catch (RuntimeException e) {
                    Map<String, String> status = new HashMap<>();
                    status.put(env, errorStatusDecoder.decodeRuntimeException(e));
                    appendResult(status, "1", result);
                }
            });
        }

        progress.setAaregStatus(result.length() > 1 ? result.substring(1) : null);
    }

    @Override
    public void release(List<String> identer) {
        identer.forEach(aaregConsumer::slettArbeidsforholdFraAlleMiljoer);
    }

    private static StringBuilder appendResult(Map<String, String> result, String arbeidsforholdId, StringBuilder builder) {
        for (Map.Entry<String, String> entry : result.entrySet()) {
            builder.append(',')
                    .append(entry.getKey())
                    .append(": arbforhold=")
                    .append(arbeidsforholdId)
                    .append('$')
                    .append(entry.getValue().replaceAll(",", "&").replaceAll(":", "="));
        }
        return builder;
    }
}