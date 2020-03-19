package no.nav.registre.inntekt.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektSyntConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektstubV2Consumer;
import no.nav.registre.inntekt.consumer.rs.TestnorgeAaregConsumer;
import no.nav.registre.inntekt.domain.RsInntekt;
import no.nav.registre.inntekt.domain.altinn.AltinnInntektRequest;
import no.nav.registre.inntekt.domain.altinn.RsArbeidsforhold;
import no.nav.registre.inntekt.domain.altinn.RsArbeidsgiver;
import no.nav.registre.inntekt.domain.altinn.RsAvsendersystem;
import no.nav.registre.inntekt.domain.altinn.RsBeregnetInntekt;
import no.nav.registre.inntekt.domain.altinn.RsKontaktinformasjon;
import no.nav.registre.inntekt.provider.rs.requests.AltinnDollyRequest;
import no.nav.registre.inntekt.provider.rs.requests.AltinnRequest;
import no.nav.registre.inntekt.utils.ValidationException;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AltinnInntektService {

    private static final int MINIMUM_ALDER = 13;

    private static final String JSON_NODE_OPPLYSNINGSPLIKTIG = "opplysningspliktig";
    private static final String JSON_NODE_TYPE = "type";
    private static final String JSON_NODE_ORGANISASJONSNUMMER = "organisasjonsnummer";
    private static final String JSON_NODE_OFFENTLIG_IDENT = "offentligIdent";
    private static final String JSON_NODE_ARBEIDSFORHOLD_ID = "arbeidsforholdId";

    private final HodejegerenConsumer hodejegerenConsumer;
    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;
    private final InntektSyntConsumer inntektSyntConsumer;
    private final TestnorgeAaregConsumer testnorgeAaregConsumer;
    private final InntektstubV2Consumer inntektstubV2Consumer;

    private final Boolean NAER_RELASJON = false;
    private final String YTELSE = "YTELSE PLASSHOLDER";
    private final String AARSAK_TIL_INNSENDING = "MÅNEDSOPPGAVE";
    private final String KONTAKTINFORMASJON_TELEFONNUMMER = "12345678";
    private final String KONTAKTINFORMASJON_NAVN = "KONTAKTINFORMASJON NAVN PLASSHOLDER";
    private final String AVSENDERSYSTEM_SYSTEMNAVN = "DOLLY";
    private final String AVSENDERSYSTEM_SYSTEMVERSJON = "0.0.0";
    private final String ARBEIDSFORHOLD_AARSAK_VED_ENDRING = "ÅRSAK VED ENDRING PLASSHOLDER";

    public Map<String, List<RsInntekt>> lagAltinnMeldinger(
            AltinnRequest altinnRequest,
            boolean opprettPaaEksisterende,
            boolean sendTilInntektskomponenten,
            Integer prosentSomStemmerMedInntektskomponenten
    ) {
        var populasjonsStroerrelse = hentLevendeIdenterOverAlder(altinnRequest.getAvspillergruppeId()).size();
        var identer = identerSomSkalHaInntekt(opprettPaaEksisterende, altinnRequest.getAvspillergruppeId(), altinnRequest.getMiljoe());


        return null;
    }

    public List<String> lagAltinnMeldinger(AltinnDollyRequest dollyRequest) throws ValidationException {
        var miljoe = dollyRequest.getMiljoe();
        var ident = dollyRequest.getArbeidstakerFnr();
        var inntekterAaOpprette = dollyRequest.getInntekter();

        var arbeidsforholdListe = testnorgeAaregConsumer.hentArbeidsforholdTilIdentIMiljoe(ident, miljoe);
        if (arbeidsforholdListe == null || arbeidsforholdListe.isEmpty()) {
            throw new ValidationException(Collections.singletonList("Kunne ikke finne arbeidsforhold for ident " + ident));
        }

        List<AltinnInntektRequest> altinnInntektRequests = new ArrayList<>(inntekterAaOpprette.size());
        // TODO: Refaktorering -- dersom én av virksomhetsnumrene feiler, kutter alle. Beholder for MVP
        for (var inntekt : inntekterAaOpprette) {
            var nyesteArbeidsforhold = finnNyesteArbeidsforholdIOrganisasjon(ident, inntekt.getVirksomhetsnummer(), arbeidsforholdListe);
            altinnInntektRequests.add(AltinnInntektRequest.builder()
                    .aarsakTilInnsending(AARSAK_TIL_INNSENDING)
                    .arbeidstakerFnr(ident)
                    .ytelse(YTELSE)
                    .arbeidsgiver(
                            RsArbeidsgiver.builder()
                                    .virksomhetsnummer(inntekt.getVirksomhetsnummer())
                                    .kontaktinformasjon(RsKontaktinformasjon.builder()
                                            .kontaktinformasjonNavn(KONTAKTINFORMASJON_NAVN)
                                            .telefonnummer(KONTAKTINFORMASJON_TELEFONNUMMER).build())
                                    .build())
                    .avsendersystem(
                            RsAvsendersystem.builder()
                                    .systemnavn(AVSENDERSYSTEM_SYSTEMNAVN)
                                    .systemversjon(AVSENDERSYSTEM_SYSTEMVERSJON)
                                    .innsendingstidspunkt(inntekt.getDato().atStartOfDay())
                                    .build())
                    .arbeidsforhold(
                            RsArbeidsforhold.builder()
                                    .arbeidsforholdId(nyesteArbeidsforhold.findValue(JSON_NODE_ARBEIDSFORHOLD_ID).asText())
                                    .beregnetInntekt(RsBeregnetInntekt.builder()
                                            .aarsakVedEndring(ARBEIDSFORHOLD_AARSAK_VED_ENDRING)
                                            .beloep(inntekt.getBeloep())
                                            .build())
                                    .build())
                    .build());
        }


    }

    private JsonNode finnNyesteArbeidsforholdIOrganisasjon (
            String ident,
            String organisasjonsnummer,
            List<JsonNode> arbeidsforholdsListe) throws ValidationException {
        var arbeidsforholdMedOppgittOrgnrListe = arbeidsforholdsListe.stream()
                .filter(arbeidsforhold ->
                    arbeidsforhold.findValue(JSON_NODE_OPPLYSNINGSPLIKTIG).findValue(JSON_NODE_ORGANISASJONSNUMMER).asText().equals(organisasjonsnummer) ||
                            arbeidsforhold.findValue(JSON_NODE_OPPLYSNINGSPLIKTIG).findValue(JSON_NODE_OFFENTLIG_IDENT).asText().equals(organisasjonsnummer))
                .collect(Collectors.toList());

        if (arbeidsforholdMedOppgittOrgnrListe.isEmpty()) {
            throw new ValidationException(Collections.singletonList("Ingen arbeidsforhold hos organisasjonsnummer: \"" +
                    organisasjonsnummer + "\" kunne bli funnet for ident: \"" + ident + "\"."));
        }
        return finnNyesteArbeidsforhold(arbeidsforholdMedOppgittOrgnrListe);
    }

    private JsonNode finnNyesteArbeidsforhold(List<JsonNode> arbeidsforhold) {
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

    private List<String> identerSomSkalHaInntekt(boolean opprettPaaEksisterende, Long avspillergruppeId, String miljoe) {
        var arbeidereMedInntekt = hentArbeidereMedInntekt(avspillergruppeId, miljoe);

        return new ArrayList<>();
    }

    private List<String> hentLevendeIdenterOverAlder(Long avspillergruppeId) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER);
    }

    private HashSet hentArbeidereMedInntekt(Long avspillergruppeId, String miljoe) {

        var identerIAareg = new HashSet<>(testnorgeAaregConsumer.hentIdenterIAvspillergruppeMedArbeidsforhold(avspillergruppeId, miljoe));
        var identerIInntektstub = new HashSet<>(inntektstubV2Consumer.hentEksisterendeIdenter());
        identerIInntektstub.retainAll(identerIAareg);

        return identerIInntektstub;
    }


}
