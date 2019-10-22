package no.nav.registre.aareg.service;

import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Map;

import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.domain.Arbeidsforhold;

public abstract class AaregAbstractClient {

    protected static final String ARBEIDSGIVER = "arbeidsgiver";
    protected static final String ARBEIDSTAKER = "arbeidstaker";

    protected static RsAaregOppdaterRequest buildRequest(Arbeidsforhold arbfInput, String env) {
        RsAaregOppdaterRequest request = new RsAaregOppdaterRequest();
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

    protected static String getArbeidsforholdType(Map arbeidsforhold) {
        return (String) arbeidsforhold.get("type");
    }

    protected static String getPeriodeFom(Map arbeidsforhold) {
        return (String) ((Map) ((Map) arbeidsforhold.get("ansettelsesperiode")).get("periode")).get("fom");
    }

    protected static String getOffentligIdent(Map arbeidforhold) {
        return (String) ((Map) arbeidforhold.get(ARBEIDSTAKER)).get("offentligIdent");
    }

    protected static String getYrkeskode(Map arbeidforhold) {
        return (String) ((Map) ((List) arbeidforhold.get("arbeidsavtaler")).get(0)).get("yrke");
    }
}
