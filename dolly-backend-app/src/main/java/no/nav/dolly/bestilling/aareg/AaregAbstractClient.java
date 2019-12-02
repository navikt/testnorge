package no.nav.dolly.bestilling.aareg;

import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;

import java.util.Map;

import no.nav.dolly.bestilling.aareg.domain.AaregOppdaterRequest;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;

public abstract class AaregAbstractClient {

    protected static final String ARBEIDSGIVER = "arbeidsgiver";

    protected static AaregOppdaterRequest buildRequest(Arbeidsforhold arbfInput, String env) {
        AaregOppdaterRequest request = new AaregOppdaterRequest();
        request.setRapporteringsperiode(now());
        request.setArbeidsforhold(arbfInput);
        request.setEnvironments(singletonList(env));
        return request;
    }

    protected static String getOrgnummer(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEIDSGIVER)).get("organisasjonsnummer");
    }

    protected static String getPersonnummer(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEIDSGIVER)).get("offentligIdent");
    }

    protected static String getArbeidsgiverType(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEIDSGIVER)).get("type");
    }

    protected static Long getNavArbfholdId(Map arbeidsforhold) {
        return Long.valueOf((Integer) arbeidsforhold.get("navArbeidsforholdId"));
    }

    protected static String getArbforholdId(Map arbeidsforhold) {
        return (String) arbeidsforhold.get("arbeidsforholdId");
    }
}