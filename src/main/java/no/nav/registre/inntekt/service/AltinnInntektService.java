package no.nav.registre.inntekt.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.inntekt.consumer.rs.AltinnInntektConsumer;
import no.nav.registre.inntekt.consumer.rs.DokmotConsumer;
import no.nav.registre.inntekt.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektSyntConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektstubV2Consumer;
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
import no.nav.registre.inntekt.utils.FilVerktoey;
import no.nav.registre.inntekt.utils.ValidationException;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
public class AltinnInntektService {

    private final HodejegerenConsumer hodejegerenConsumer;
    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;
    private final InntektSyntConsumer inntektSyntConsumer;
    private final AaregService aaregService;
    private final InntektstubV2Consumer inntektstubV2Consumer;
    private final AltinnInntektConsumer altinnInntektConsumer;
    private final DokmotConsumer dokmotConsumer;

    private static final String EIER = "ORKESTRATOREN";
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

    private static final String DUMMY_PDF_FILEPATH = "dummy.pdf";
    private static final File dummyPdf;
    static {
        try {
            dummyPdf = FilVerktoey.lastRessurs(DUMMY_PDF_FILEPATH);
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke initialisere klassen pga IOException ved lasting av " + DUMMY_PDF_FILEPATH, e);
        }
    }

    public AltinnInntektService(
            HodejegerenConsumer hodejegerenConsumer,
            HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer,
            InntektSyntConsumer inntektSyntConsumer,
            AaregService aaregService,
            InntektstubV2Consumer inntektstubV2Consumer,
            AltinnInntektConsumer altinnInntektConsumer,
            DokmotConsumer dokmotConsumer
    ) {
        this.hodejegerenConsumer = hodejegerenConsumer;
        this.hodejegerenHistorikkConsumer = hodejegerenHistorikkConsumer;
        this.inntektSyntConsumer = inntektSyntConsumer;
        this.aaregService = aaregService;
        this.inntektstubV2Consumer = inntektstubV2Consumer;
        this.altinnInntektConsumer = altinnInntektConsumer;
        this.dokmotConsumer = dokmotConsumer;
    }




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

        var arbeidsforholdListe = aaregService.hentArbeidsforhold(ident, miljoe);
        if (arbeidsforholdListe == null || arbeidsforholdListe.isEmpty()) {
            throw new ValidationException("Kunne ikke finne arbeidsforhold for ident " + ident);
        }

        // TODO: Refaktorering -- dersom én av virksomhetsnumrene feiler, kutter alle. Beholder for MVP
        var altinnInntektMeldinger = new ArrayList<String>(inntekterAaOpprette.size());
        for (var inntekt : inntekterAaOpprette) {
            var nyesteArbeidsforhold = AaregService.finnNyesteArbeidsforholdIOrganisasjon(ident, inntekt.getVirksomhetsnummer(), arbeidsforholdListe);
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
                            .arbeidsforholdId(AaregService.finnArbeidsforholdId(nyesteArbeidsforhold))
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
                                            .fysiskDokument(FilVerktoey.encodeFilTilBase64Binary(dummyPdf))
                                            .build())).build())).build();
            dokmotConsumer.opprettJournalpost(dokmotRequest, miljoe);
        }
        return altinnInntektMeldinger;
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
