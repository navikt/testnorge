package no.nav.registre.inntekt.service;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

import no.nav.registre.inntekt.consumer.rs.TestnorgeAaregConsumer;
import no.nav.registre.inntekt.utils.ValidationException;
// import no.nav.tjenester.aordningen.arbeidsforhold.v1.Arbeidsforhold;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AaregService {

    private final TestnorgeAaregConsumer testnorgeAaregConsumer;

    private static final String JSON_NODE_OPPLYSNINGSPLIKTIG = "opplysningspliktig";
    private static final String JSON_NODE_TYPE = "type";
    private static final String JSON_NODE_ARBEIDSGIVER = "arbeidsgiver";
    private static final String JSON_NODE_ORGANISASJONSNUMMER = "organisasjonsnummer";
    private static final String JSON_NODE_OFFENTLIG_IDENT = "offentligIdent";
    private static final String JSON_NODE_ARBEIDSFORHOLD_ID = "arbeidsforholdId";
    private static final String TYPE_ORGANISASJON = "Organisasjon";
    private static final String TYPE_PERSON = "Person";

    public List<JsonNode> hentArbeidsforhold(String ident, String miljoe) {
        return testnorgeAaregConsumer.hentArbeidsforholdTilIdentIMiljoe(ident, miljoe);
    }

    public List<String> hentIdenterMedArbeidsforhold(Long avspillergruppeId, String miljoe) {
        return testnorgeAaregConsumer.hentIdenterIAvspillergruppeMedArbeidsforhold(avspillergruppeId, miljoe);
    }

    public static JsonNode finnNyesteArbeidsforholdIOrganisasjon (
            String ident,
            String organisasjonsnummer,
            List<JsonNode> arbeidsforholdsListe) throws ValidationException {

        var arbeidsforholdMedOppgittOrgnrListe = new ArrayList<JsonNode>();
        for (var arbeidsforhold : arbeidsforholdsListe) {
            if (arbeidsforhold.findValue(JSON_NODE_OPPLYSNINGSPLIKTIG).findValue("type").asText().equals(TYPE_ORGANISASJON)) {
                if (arbeidsforhold.findValue(JSON_NODE_OPPLYSNINGSPLIKTIG).findValue(JSON_NODE_ORGANISASJONSNUMMER).asText().equals(organisasjonsnummer)) {
                    arbeidsforholdMedOppgittOrgnrListe.add(arbeidsforhold);
                }
            } else if (arbeidsforhold.findValue(JSON_NODE_OPPLYSNINGSPLIKTIG).findValue("type").asText().equals(TYPE_PERSON) &&
                    arbeidsforhold.findValue(JSON_NODE_OPPLYSNINGSPLIKTIG).findValue(JSON_NODE_OFFENTLIG_IDENT).asText().equals(organisasjonsnummer)) {
                arbeidsforholdMedOppgittOrgnrListe.add(arbeidsforhold);
            }
        }

        if (arbeidsforholdMedOppgittOrgnrListe.isEmpty()) {
            throw new ValidationException("Ingen arbeidsforhold hos organisasjonsnummer: \"" +
                    organisasjonsnummer + "\" kunne bli funnet for ident: \"" + ident + "\".");
        }
        return finnNyesteArbeidsforhold(arbeidsforholdMedOppgittOrgnrListe);
    }

    public static JsonNode finnNyesteArbeidsforhold(List<JsonNode> arbeidsforhold) {
        var nyesteDato = LocalDateTime.MIN;
        JsonNode nyesteArbeidsforhold = null;
        for (var arbeidsforholdet : arbeidsforhold) {
            var opprettetTidspunkt = LocalDateTime.parse(arbeidsforholdet.findValue("opprettetTidspunkt").asText());
            if (opprettetTidspunkt.isAfter(nyesteDato)) {
                nyesteDato = opprettetTidspunkt;
                nyesteArbeidsforhold = arbeidsforholdet;
            }
        }
        return nyesteArbeidsforhold;
    }

    public static String finnArbeidsforholdId(JsonNode arbeidsforhold) {
        return arbeidsforhold.findValue(JSON_NODE_ARBEIDSFORHOLD_ID).asText();
    }

    /* TODO: Endre fra JsonNode til Arbeidsforhold fra aordningen. Problem her er at man ikke har tilgang på organisasjonsnummer fra denne datastrukturen.
    public static Arbeidsforhold finnNyesteArbeidsforhold(List<Arbeidsforhold> arbeisforholdListe) {
        var nyesteDato = LocalDateTime.MIN;
        Arbeidsforhold nyesteArbeidsforhold = null;

        for (var arbeidsforhold : arbeisforholdListe) {
            var registreringsTidspunkt = arbeidsforhold.getRegistrert();

            if (registreringsTidspunkt.isAfter(nyesteDato)) {
                nyesteDato = registreringsTidspunkt;
                nyesteArbeidsforhold = arbeidsforhold;
            }
        }

        return nyesteArbeidsforhold;
    }

    public static Arbeidsforhold finnNyesteArbeidsforholdIOrganisasjon(
            String ident,
            String organisasjonsnummer,
            List<Arbeidsforhold> arbeidsforholdsListe
    ) {
        var arbeidsforholdMedOppgittOrgnrListe = new ArrayList<Arbeidsforhold>();

        for (var arbeidsforhold : arbeidsforholdsListe) {

            if (arbeidsforhold.getOpplysningspliktig())
        }
    }
    */
}
