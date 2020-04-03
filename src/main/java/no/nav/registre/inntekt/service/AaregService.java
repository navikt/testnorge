package no.nav.registre.inntekt.service;

import lombok.RequiredArgsConstructor;

import no.nav.registre.inntekt.consumer.rs.TestnorgeAaregConsumer;
import no.nav.registre.inntekt.utils.ValidationException;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Arbeidsforhold;

import no.nav.tjenester.aordningen.arbeidsforhold.v1.Organisasjon;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Person;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AaregService {

    private final TestnorgeAaregConsumer testnorgeAaregConsumer;

    private static final String TYPE_ORGANISASJON = "Organisasjon";
    private static final String TYPE_PERSON = "Person";

    public List<Arbeidsforhold> hentArbeidsforhold(String ident, String miljoe) {
        return testnorgeAaregConsumer.hentArbeidsforholdTilIdentIMiljoe(ident, miljoe);
    }

    public List<String> hentIdenterMedArbeidsforhold(Long avspillergruppeId, String miljoe) {
        return testnorgeAaregConsumer.hentIdenterIAvspillergruppeMedArbeidsforhold(avspillergruppeId, miljoe);
    }

    public static Arbeidsforhold finnNyesteArbeidsforholdIOrganisasjon (
            String ident,
            String organisasjonsnummer,
            List<Arbeidsforhold> arbeidsforholdsListe
    ) throws ValidationException {

        var arbeidsforholdMedOppgittOrgnrListe = new ArrayList<Arbeidsforhold>();
        for (var arbeidsforhold : arbeidsforholdsListe) {
            if (isValidOrganisasjonsnummer(arbeidsforhold, organisasjonsnummer)) {
                arbeidsforholdMedOppgittOrgnrListe.add(arbeidsforhold);
            }
        }

        if (arbeidsforholdMedOppgittOrgnrListe.isEmpty()) {
            throw new ValidationException("Ingen arbeidsforhold hos organisasjonsnummer: \"" +
                    organisasjonsnummer + "\" kunne bli funnet for ident: \"" + ident + "\".");
        }
        return finnNyesteArbeidsforhold(arbeidsforholdMedOppgittOrgnrListe);
    }

    // TODO: simplify
    private static boolean isValidOrganisasjonsnummer(Arbeidsforhold arbeidsforhold, String organisasjonsnummer) {

        if (arbeidsforhold.getOpplysningspliktig().getType().equals(TYPE_ORGANISASJON)) {
            Organisasjon org = (Organisasjon)arbeidsforhold.getOpplysningspliktig();
            if (org.getOrganisasjonsnummer().equals(organisasjonsnummer)) {
                return true;
            }
        }

        if (arbeidsforhold.getOpplysningspliktig().getType().equals(TYPE_PERSON)) {
            Person pers = (Person)arbeidsforhold.getOpplysningspliktig();
            if (pers.getOffentligIdent().equals(organisasjonsnummer)) {
                return true;
            }
        }

        if (arbeidsforhold.getArbeidsgiver().getType().equals(TYPE_ORGANISASJON)) {
            Organisasjon org = (Organisasjon)arbeidsforhold.getArbeidsgiver();
            if (org.getOrganisasjonsnummer().equals(organisasjonsnummer)) {
                return true;
            }
        }

        if (arbeidsforhold.getArbeidsgiver().getType().equals(TYPE_PERSON)) {
            Person pers = (Person)arbeidsforhold.getArbeidsgiver();
            if (pers.getOffentligIdent().equals(organisasjonsnummer)) {
                return true;
            }
        }

        return false;
    }

    public static Arbeidsforhold finnNyesteArbeidsforhold(List<Arbeidsforhold> arbeidsforhold) {
        var nyesteDato = LocalDateTime.MIN;
        Arbeidsforhold nyesteArbeidsforhold = null;
        for (var arbeidsforholdet : arbeidsforhold) {
            var opprettetTidspunkt = arbeidsforholdet.getAnsettelsesperiode().getSporingsinformasjon().getOpprettetTidspunkt();
            if (opprettetTidspunkt.isAfter(nyesteDato)) {
                nyesteDato = opprettetTidspunkt;
                nyesteArbeidsforhold = arbeidsforholdet;
            }
        }
        return nyesteArbeidsforhold;
    }

    public static String finnArbeidsforholdId(Arbeidsforhold arbeidsforhold) {
        return arbeidsforhold.getArbeidsforholdId();
    }
}
