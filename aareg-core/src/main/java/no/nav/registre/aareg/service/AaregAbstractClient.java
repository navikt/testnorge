package no.nav.registre.aareg.service;

import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Map;

import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.domain.RsArbeidsforhold;

abstract class AaregAbstractClient {

    private static final String ARBEIDSGIVER = "arbeidsgiver";
    private static final String ARBEIDSTAKER = "arbeidstaker";

    static RsAaregOppdaterRequest buildRequest(RsArbeidsforhold arbfInput, String env) {
        RsAaregOppdaterRequest request = new RsAaregOppdaterRequest();
        request.setRapporteringsperiode(now());
        request.setArbeidsforhold(arbfInput);
        request.setEnvironments(singletonList(env));
        return request;
    }

    static String getOrgnummer(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEIDSGIVER)).get("organisasjonsnummer");
    }

    static String getPersonnummer(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEIDSGIVER)).get("offentligIdent");
    }

    static String getArbeidsgiverType(Map arbeidsforhold) {
        return (String) ((Map) arbeidsforhold.get(ARBEIDSGIVER)).get("type");
    }

    static Long getNavArbfholdId(Map arbeidsforhold) {
        return Long.valueOf((Integer) arbeidsforhold.get("navArbeidsforholdId"));
    }

    static String getArbforholdId(Map arbeidsforhold) {
        return (String) arbeidsforhold.get("arbeidsforholdId");
    }

    static String getArbeidsforholdType(Map arbeidsforhold) {
        return (String) arbeidsforhold.get("type");
    }

    static String getPeriodeFom(Map arbeidsforhold) {
        return (String) ((Map) ((Map) arbeidsforhold.get("ansettelsesperiode")).get("periode")).get("fom");
    }

    static String getOffentligIdent(Map arbeidforhold) {
        return (String) ((Map) arbeidforhold.get(ARBEIDSTAKER)).get("offentligIdent");
    }

    static String getYrkeskode(Map arbeidforhold) {
        return (String) ((Map) ((List) arbeidforhold.get("arbeidsavtaler")).get(0)).get("yrke");
    }
}
