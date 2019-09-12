package no.nav.dolly.consumer.aareg;

import static java.util.Collections.singletonList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.aareg.AaregAbstractClient;
import no.nav.dolly.aareg.AaregReleaseIdentClient;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAktoer;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPersonAareg;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class AaregClient extends AaregAbstractClient implements ClientRegister {

    private static final String ARBEIDSGIVER = "arbeidsgiver";

    private final AaregRestConsumer aaregRestConsumer;
    private final AaregWsConsumer aaregWsConsumer;
    private final AaregReleaseIdentClient aaregReleaseIdentClient;

    @Override
    public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (bestilling.getAareg() == null || bestilling.getAareg().isEmpty()) {
            progress.setAaregStatus(null);
            return;
        }

        StringBuilder result = new StringBuilder();

        bestilling.getEnvironments().forEach(env -> {

            Object arbeidsforhold = getArbeidsforhold(norskIdent, env);

            AtomicInteger arbeidsForholdId = new AtomicInteger(1);
            bestilling.getAareg().forEach(arbfInput -> {
                arbfInput.setArbeidsforholdID(Integer.toString(arbeidsForholdId.getAndIncrement()));
                arbfInput.setArbeidstaker(RsPersonAareg.builder().ident(norskIdent.getIdent()).build());

                if (arbeidsforhold != null && equalArbeidsforhold(arbfInput, (Map) arbeidsforhold)) {
                    Map arbfFraAareg = (Map) arbeidsforhold;
                    arbfInput.setArbeidsforholdIDnav(getNavArbfholdId(arbfFraAareg));
                    appendResult(aaregWsConsumer.oppdaterArbeidsforhold(buildRequest(arbfInput, env)), arbfInput.getArbeidsforholdID(), result);
                } else {
                    appendResult(aaregWsConsumer.opprettArbeidsforhold(RsAaregOpprettRequest.builder()
                            .arbeidsforhold(arbfInput)
                            .environments(singletonList(env))
                            .build()), arbfInput.getArbeidsforholdID(), result);
                }
            });

        });

        progress.setAaregStatus(result.length() > 1 ? result.substring(1) : null);

    }

    @Override
    public void release(List<String> identer) {
        identer.forEach(aaregReleaseIdentClient::deleteArbeidsforhold);
    }

    private void appendResult(Map<String, String> result, String arbeidsforholdId, StringBuilder builder) {
        for (Map.Entry<String, String> entry : result.entrySet()) {
            builder.append(',')
                    .append(entry.getKey())
                    .append(": arbforhold=")
                    .append(arbeidsforholdId)
                    .append('$')
                    .append(entry.getValue().replace(",", "&").replace(":", "="));
        }
    }

    private boolean equalArbeidsforhold(RsArbeidsforhold arbfInput, Map arbfFraAareg) {
        return (isMatchArbgivOrgnummer(arbfInput.getArbeidsgiver(), getIdentifyingNumber(arbfFraAareg)) ||
                isMatchArbgivPersonnummer(arbfInput.getArbeidsgiver(), getIdentifyingNumber(arbfFraAareg))) &&
                arbfInput.getArbeidsforholdID().equals(getArbforholdId(arbfFraAareg));
    }

    private boolean isMatchArbgivOrgnummer(RsAktoer arbeidsgiver, String orgnummer) {
        return arbeidsgiver instanceof RsOrganisasjon &&
                ((RsOrganisasjon) arbeidsgiver).getOrgnummer().equals(orgnummer);
    }

    private boolean isMatchArbgivPersonnummer(RsAktoer arbeidsgiver, String ident) {
        return arbeidsgiver instanceof RsAktoerPerson &&
                ((RsAktoerPerson) arbeidsgiver).getIdent().equals(ident);
    }

    private String getIdentifyingNumber(Map arbfFraAareg) {
        return "Organisasjon".equals(getType(arbfFraAareg)) ? getOrgnummer(arbfFraAareg) : getPersonnummer(arbfFraAareg);
    }

    private Object getArbeidsforhold(NorskIdent norskIdent, String env) {
        Object[] response = new Object[0];
        try {
            response = aaregRestConsumer.readArbeidsforhold(norskIdent.getIdent(), env).getBody();
        } catch (RuntimeException e) {
            log.error("Lesing av aareg i {} feilet, {}", env, e.getLocalizedMessage());
        }
        return response != null && response.length > 0 ? response[0] : null;
    }

    private String getType(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEIDSGIVER)).get("type");
    }

}
