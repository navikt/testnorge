package no.nav.registre.inntekt.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.registre.inntekt.consumer.rs.AltinnInntektConsumer;
import no.nav.registre.inntekt.consumer.rs.DokmotConsumer;
import no.nav.registre.inntekt.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektSyntConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektstubV2Consumer;
import no.nav.registre.inntekt.consumer.rs.TestnorgeAaregConsumer;
import no.nav.registre.inntekt.domain.altinn.rs.AltinnInntektRequest;
import no.nav.registre.inntekt.domain.altinn.rs.RsArbeidsforhold;
import no.nav.registre.inntekt.domain.altinn.rs.RsArbeidsgiver;
import no.nav.registre.inntekt.domain.altinn.rs.RsAvsendersystem;
import no.nav.registre.inntekt.domain.altinn.rs.RsBeregnetInntekt;
import no.nav.registre.inntekt.domain.altinn.rs.RsKontaktinformasjon;
import no.nav.registre.inntekt.domain.dokmot.AvsenderMottaker;
import no.nav.registre.inntekt.domain.dokmot.Bruker;
import no.nav.registre.inntekt.domain.dokmot.Dokument;
import no.nav.registre.inntekt.domain.dokmot.Dokumentvariant;
import no.nav.registre.inntekt.provider.rs.requests.AltinnDollyRequest;
import no.nav.registre.inntekt.provider.rs.requests.DokmotRequest;
import no.nav.registre.inntekt.utils.ValidationException;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AltinnInntektService {

    private static final int MINIMUM_ALDER = 13;

    private static final String JSON_NODE_OPPLYSNINGSPLIKTIG = "opplysningspliktig";
    private static final String JSON_NODE_TYPE = "type";
    private static final String JSON_NODE_ARBEIDSGIVER = "arbeidsgiver";
    private static final String JSON_NODE_ORGANISASJONSNUMMER = "organisasjonsnummer";
    private static final String JSON_NODE_OFFENTLIG_IDENT = "offentligIdent";
    private static final String JSON_NODE_ARBEIDSFORHOLD_ID = "arbeidsforholdId";
    private static final String TYPE_ORGANISASJON = "Organisasjon";
    private static final String TYPE_PERSON = "Person";

    private final HodejegerenConsumer hodejegerenConsumer;
    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;
    private final InntektSyntConsumer inntektSyntConsumer;
    private final TestnorgeAaregConsumer testnorgeAaregConsumer;
    private final InntektstubV2Consumer inntektstubV2Consumer;
    private final AltinnInntektConsumer altinnInntektConsumer;
    private final DokmotConsumer dokmotConsumer;

    private static final String EIER = "DOLLY";
    private static final Boolean NAER_RELASJON = false;
    private static final String YTELSE = "Sykepenger";
    private static final String AARSAK_TIL_INNSENDING = "Endring";
    private static final String KONTAKTINFORMASJON_TELEFONNUMMER = "12345678";
    private static final String KONTAKTINFORMASJON_NAVN = "GUL BOLLE";
    private static final String AVSENDERSYSTEM_SYSTEMNAVN = "DOLLY";
    private static final String AVSENDERSYSTEM_SYSTEMVERSJON = "0.0.0";
    private static final String ARBEIDSFORHOLD_AARSAK_VED_ENDRING = "Tariffendring";

    private static final String JOURNALPOST_TYPE = "INNGAAENDE";
    private static final String AVSENDER_MOTTAKER_ID_TYPE = "ORGNR";
    private static final String BRUKER_ID_TYPE = "FNR";
    private static final String TEMA = "FOR";
    private static final String TITTEL = "Syntetisk Inntektsmelding";
    private static final String KANAL = "ALTINN";
    private static final String DOKUMENTER_BREVKODE = "4936";
    private static final String DOKUMENTER_BERVKATEGORI = "ES";

    private final String DUMMY_PDF_FILEPATH = "dummy.pdf";


    /*public Map<String, List<RsInntekt>> lagAltinnMeldinger(
            AltinnRequest altinnRequest,
            boolean opprettPaaEksisterende,
            boolean sendTilInntektskomponenten,
            Integer prosentSomStemmerMedInntektskomponenten
    ) {
        var populasjonsStroerrelse = hentLevendeIdenterOverAlder(altinnRequest.getAvspillergruppeId()).size();
        var identer = identerSomSkalHaInntekt(opprettPaaEksisterende, altinnRequest.getAvspillergruppeId(), altinnRequest.getMiljoe());


        return null;
    }*/

    public List<String> lagAltinnMeldinger(AltinnDollyRequest dollyRequest) throws ValidationException {
        var miljoe = dollyRequest.getMiljoe();
        var ident = dollyRequest.getArbeidstakerFnr();
        var inntekterAaOpprette = dollyRequest.getInntekter();

        var arbeidsforholdListe = testnorgeAaregConsumer.hentArbeidsforholdTilIdentIMiljoe(ident, miljoe);
        if (arbeidsforholdListe == null || arbeidsforholdListe.isEmpty()) {
            throw new ValidationException(Collections.singletonList("Kunne ikke finne arbeidsforhold for ident " + ident));
        }

        // TODO: Refaktorering -- dersom én av virksomhetsnumrene feiler, kutter alle. Beholder for MVP
        var altinnInntektMeldinger = new ArrayList<String>(inntekterAaOpprette.size());
        for (var inntekt : inntekterAaOpprette) {
            var nyesteArbeidsforhold = finnNyesteArbeidsforholdIOrganisasjon(ident, inntekt.getVirksomhetsnummer(), arbeidsforholdListe);
            if (Objects.isNull(nyesteArbeidsforhold)) {
                continue;
            }
            var altinnInntektRequest = AltinnInntektRequest.builder()
                    .aarsakTilInnsending(AARSAK_TIL_INNSENDING)
                    .arbeidstakerFnr(ident)
                    .ytelse(YTELSE)
                    .arbeidsgiver(RsArbeidsgiver.builder()
                            .virksomhetsnummer(inntekt.getVirksomhetsnummer())
                            .kontaktinformasjon(RsKontaktinformasjon.builder()
                                    .kontaktinformasjonNavn(KONTAKTINFORMASJON_NAVN)
                                    .telefonnummer(KONTAKTINFORMASJON_TELEFONNUMMER).build()).build())
                    .avsendersystem(RsAvsendersystem.builder()
                            .systemnavn(AVSENDERSYSTEM_SYSTEMNAVN)
                            .systemversjon(AVSENDERSYSTEM_SYSTEMVERSJON)
                            .innsendingstidspunkt(inntekt.getDato().atStartOfDay())
                            .build())
                    .arbeidsforhold(RsArbeidsforhold.builder()
                            .arbeidsforholdId(nyesteArbeidsforhold.findValue(JSON_NODE_ARBEIDSFORHOLD_ID).asText())
                            .beregnetInntekt(RsBeregnetInntekt.builder()
                                    .aarsakVedEndring(ARBEIDSFORHOLD_AARSAK_VED_ENDRING)
                                    .beloep(inntekt.getBeloep())
                                    .build()).build()).build();

            // Lag melding
            var altinnInntektResponse = altinnInntektConsumer.getInntektsmeldingXml201812(altinnInntektRequest);
            altinnInntektMeldinger.add(altinnInntektResponse);

            // Lagre melding i Joark
            var dokmotRequest = DokmotRequest.builder()
                    .journalposttype(JOURNALPOST_TYPE)
                    .avsenderMottaker(AvsenderMottaker.builder()
                            .id(inntekt.getVirksomhetsnummer())
                            .idType(AVSENDER_MOTTAKER_ID_TYPE)
                            .navn(KONTAKTINFORMASJON_NAVN).build())
                    .bruker(Bruker.builder()
                            .id(ident)
                            .idType(BRUKER_ID_TYPE).build())
                    .tema(TEMA)
                    .tittel(TITTEL)
                    .kanal(KANAL)
                    .eksternReferanseId("INGEN")
                    .datoMottatt(Date.from(inntekt.getDato().atStartOfDay(ZoneId.systemDefault()).toInstant())) // Skal være java util Date
                    .dokumenter(Collections.singletonList(Dokument.builder()
                            .brevkode(DOKUMENTER_BREVKODE)
                            .dokumentkategori(DOKUMENTER_BERVKATEGORI)
                            .tittel(TITTEL)
                            .dokumentvarianter(Arrays.asList(
                                    Dokumentvariant.builder()
                                            .filtype("XML")
                                            .variantformat("ORIGINAL")
                                            .fysiskDokument(Base64.getEncoder().encode(altinnInntektResponse.getBytes(UTF_8)))
                                            .build(),
                                    Dokumentvariant.builder()
                                            .filtype("PDF")
                                            .variantformat("ARKIV")
                                            .fysiskDokument(encodeFilTilBase64Binary(DUMMY_PDF_FILEPATH))
                                            .build())).build())).build();
            dokmotConsumer.opprettJournalpost(dokmotRequest, miljoe);
        }
        return altinnInntektMeldinger;
    }

    private static byte[] encodeFilTilBase64Binary(String filNavn) {
        try {
            File fil = new File(filNavn);
            byte[] bytes = lastFil(fil);
            return Base64.getEncoder().encode(bytes);
        } catch (IOException e) {
            log.info("Kunne ikke finne " + filNavn);
        }

        return new byte[0];
    }

    private static byte[] lastFil(File fil) throws IOException {
        try (InputStream is = new FileInputStream(fil)) {

            long length = fil.length();
            if (length > Integer.MAX_VALUE) {
                log.info("Fil for stor for å leses.");
                throw new IOException("File too large");
            }
            byte[] bytes = new byte[(int) length];

            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            is.close();
            return bytes;
        }
    }

    private JsonNode finnNyesteArbeidsforholdIOrganisasjon (
            String ident,
            String organisasjonsnummer,
            List<JsonNode> arbeidsforholdsListe) throws ValidationException {
        /*
        var arbeidsforholdMedOppgittOrgnrListe = arbeidsforholdsListe.stream()
                .filter(arbeidsforhold ->
                    arbeidsforhold.findValue(JSON_NODE_OPPLYSNINGSPLIKTIG).findValue(JSON_NODE_ORGANISASJONSNUMMER).asText().equals(organisasjonsnummer) ||
                            arbeidsforhold.findValue(JSON_NODE_OPPLYSNINGSPLIKTIG).findValue(JSON_NODE_OFFENTLIG_IDENT).asText().equals(organisasjonsnummer))
                .collect(Collectors.toList());
         */
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

    /*private List<String> identerSomSkalHaInntekt(boolean opprettPaaEksisterende, Long avspillergruppeId, String miljoe) {
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
    }*/


}
