package no.nav.dolly.aareg;

import static java.util.Collections.singletonList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPerson;

@Service
public class AaregService {

    private static final String ARBEDISGIVER = "arbeidsgiver";
    @Autowired
    private AaregRestConsumer aaregRestConsumer;

    @Autowired
    private AaregWsConsumer aaregWsConsumer;

    public String gjenopprettArbeidsforhold(List<RsArbeidsforhold> arbeidsforholdList, List<String> environments, String ident) {

        StringBuilder result = new StringBuilder();

        if (!arbeidsforholdList.isEmpty()) {

            environments.forEach(env -> {

                ResponseEntity<Object[]> response = aaregRestConsumer.readArbeidsforhold(ident, env);

                for (int i = 0; i < arbeidsforholdList.size(); i++) {
                    RsArbeidsforhold arbfGjenopprett = arbeidsforholdList.get(i);
                    arbfGjenopprett.setArbeidsforholdID(Integer.toString(i + 1));
                    arbfGjenopprett.setArbeidstaker(RsPerson.builder().ident(ident).build());

                    boolean found = false;
                    for (int j = 0; j < response.getBody().length; j++) {
                        Map arbforhold = (Map) response.getBody()[j];
                        String orgnummer = "";
                        String personnummer = "";
                        if ("Organisasjon".equals(getType(arbforhold))) {
                            orgnummer = getOrgnummer(arbforhold);
                        }
                        if ("Person".equals(getType(arbforhold))) {
                            personnummer = getPersonnummer(arbforhold);
                        }

                        if (((arbfGjenopprett.getArbeidsgiver() instanceof RsOrganisasjon &&
                                ((RsOrganisasjon) arbfGjenopprett.getArbeidsgiver()).getOrgnummer().equals(orgnummer)) ||
                                (arbfGjenopprett.getArbeidsgiver() instanceof RsAktoerPerson &&
                                        ((RsAktoerPerson) arbfGjenopprett.getArbeidsgiver()).getIdent().equals(personnummer))) &&
                                arbfGjenopprett.getArbeidsforholdID().equals(getArbforholdId(arbforhold))) {

                            arbfGjenopprett.setArbeidsforholdIDnav(getNavArbfholdId(arbforhold));
                            RsAaregOppdaterRequest request = new RsAaregOppdaterRequest();
                            request.setRapporteringsperiode(LocalDateTime.now());
                            request.setArbeidsforhold(arbfGjenopprett);
                            request.setEnvironments(singletonList(env));

                            appendResult(aaregWsConsumer.oppdaterArbeidsforhold(request), arbfGjenopprett.getArbeidsforholdID(), result);

                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        appendResult(aaregWsConsumer.opprettArbeidsforhold(RsAaregOpprettRequest.builder()
                                .arbeidsforhold(arbfGjenopprett)
                                .environments(singletonList(env))
                                .build()), arbfGjenopprett.getArbeidsforholdID(), result);
                    }
                }
            });
        }

        return result.substring(1);
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
        return (String) ((Map) arbeidsforhold.get(ARBEDISGIVER)).get("type");
    }

    private static String getOrgnummer(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEDISGIVER)).get("organisasjonsnummer");
    }

    private static String getPersonnummer(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEDISGIVER)).get("offentligIdent");
    }

    private static Long getNavArbfholdId(Map arbeidsforhold) {
        return Long.valueOf((Integer) arbeidsforhold.get("navArbeidsforholdId"));
    }

    private static String getArbforholdId(Map arbeidsforhold) {
        return (String) arbeidsforhold.get("arbeidsforholdId");
    }
}
