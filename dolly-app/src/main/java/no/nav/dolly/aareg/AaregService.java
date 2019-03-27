package no.nav.dolly.aareg;

import static java.util.Collections.singletonList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAktoer;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPerson;
import no.nav.dolly.exceptions.DollyFunctionalException;

@Slf4j
@Service
public class AaregService {

    private static final String ARBEIDSGIVER = "arbeidsgiver";
    @Autowired
    private AaregRestConsumer aaregRestConsumer;

    @Autowired
    private AaregWsConsumer aaregWsConsumer;

    public String gjenopprettArbeidsforhold(List<RsArbeidsforhold> arbeidsforholdList, List<String> environments, String ident) {

        StringBuilder result = new StringBuilder();

        if (!arbeidsforholdList.isEmpty()) {

            environments.forEach(env -> {

                ResponseEntity<Object[]> response = ResponseEntity.ok(new Object[]{});
                try {
                    response = aaregRestConsumer.readArbeidsforhold(ident, env);
                } catch (HttpClientErrorException | DollyFunctionalException | ResourceAccessException e) {
                    log.error("Lesing av aareg i {} feilet, {}", env, e.getLocalizedMessage());
                }

                for (int i = 0; i < arbeidsforholdList.size(); i++) {
                    RsArbeidsforhold arbfInput = arbeidsforholdList.get(i);
                    arbfInput.setArbeidsforholdID(Integer.toString(i + 1));
                    arbfInput.setArbeidstaker(RsPerson.builder().ident(ident).build());

                    boolean found = false;
                    for (int j = 0; j < response.getBody().length; j++) {
                        Map arbfFraAareg = (Map) response.getBody()[j];

                        String orgIdentNummer = "Organisasjon".equals(getType(arbfFraAareg)) ? getOrgnummer(arbfFraAareg) : getPersonnummer(arbfFraAareg);

                        if ((isMatchArbgivOrgnummer(arbfInput.getArbeidsgiver(), orgIdentNummer) ||
                                isMatchArbgivPersonnummer(arbfInput.getArbeidsgiver(), orgIdentNummer)) &&
                                arbfInput.getArbeidsforholdID().equals(getArbforholdId(arbfFraAareg))) {

                            arbfInput.setArbeidsforholdIDnav(getNavArbfholdId(arbfFraAareg));
                            appendResult(aaregWsConsumer.oppdaterArbeidsforhold(buildRequest(arbfInput, env)), arbfInput.getArbeidsforholdID(), result);

                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        appendResult(aaregWsConsumer.opprettArbeidsforhold(RsAaregOpprettRequest.builder()
                                .arbeidsforhold(arbfInput)
                                .environments(singletonList(env))
                                .build()), arbfInput.getArbeidsforholdID(), result);
                    }
                }
            });
        }

        return result.length() > 1 ? result.substring(1) : null;
    }

    private static boolean isMatchArbgivOrgnummer(RsAktoer arbeidsgiver, String orgnummer) {
        return arbeidsgiver instanceof RsOrganisasjon &&
                ((RsOrganisasjon) arbeidsgiver).getOrgnummer().equals(orgnummer);
    }

    private static boolean isMatchArbgivPersonnummer(RsAktoer arbeidsgiver, String ident) {
        return arbeidsgiver instanceof RsAktoerPerson &&
                ((RsAktoerPerson) arbeidsgiver).getIdent().equals(ident);
    }

    private static RsAaregOppdaterRequest buildRequest(RsArbeidsforhold arbfInput, String env) {
        RsAaregOppdaterRequest request = new RsAaregOppdaterRequest();
        request.setRapporteringsperiode(LocalDateTime.now());
        request.setArbeidsforhold(arbfInput);
        request.setEnvironments(singletonList(env));
        return request;
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

    private static String getType(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEIDSGIVER)).get("type");
    }

    private static String getOrgnummer(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEIDSGIVER)).get("organisasjonsnummer");
    }

    private static String getPersonnummer(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEIDSGIVER)).get("offentligIdent");
    }

    private static Long getNavArbfholdId(Map arbeidsforhold) {
        return Long.valueOf((Integer) arbeidsforhold.get("navArbeidsforholdId"));
    }

    private static String getArbforholdId(Map arbeidsforhold) {
        return (String) arbeidsforhold.get("arbeidsforholdId");
    }
}
