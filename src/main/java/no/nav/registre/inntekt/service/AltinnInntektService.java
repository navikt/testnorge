package no.nav.registre.inntekt.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.inntekt.consumer.rs.AltinnInntektConsumer;
import no.nav.registre.inntekt.consumer.rs.DokmotConsumer;
import no.nav.registre.inntekt.domain.altinn.RsAltinnInntektInfo;
import no.nav.registre.inntekt.domain.altinn.rs.RsArbeidsforhold;
import no.nav.registre.inntekt.domain.altinn.rs.RsArbeidsgiver;
import no.nav.registre.inntekt.domain.altinn.rs.RsArbeidsgiverPrivat;
import no.nav.registre.inntekt.domain.altinn.rs.RsInntekt;
import no.nav.registre.inntekt.domain.altinn.rs.RsInntektsmelding;
import no.nav.registre.inntekt.domain.altinn.rs.RsKontaktinformasjon;
import no.nav.registre.inntekt.domain.altinn.rs.RsNaturalytelseDetaljer;
import no.nav.registre.inntekt.domain.altinn.rs.RsUtsettelseAvForeldrepenger;
import no.nav.registre.inntekt.domain.dokmot.AvsenderMottaker;
import no.nav.registre.inntekt.domain.dokmot.Bruker;
import no.nav.registre.inntekt.domain.dokmot.Dokument;
import no.nav.registre.inntekt.domain.dokmot.Dokumentvariant;
import no.nav.registre.inntekt.domain.dokmot.rs.RsJoarkMetadata;
import no.nav.registre.inntekt.provider.rs.requests.AltinnDollyRequest;
import no.nav.registre.inntekt.provider.rs.requests.DokmotRequest;
import no.nav.registre.inntekt.utils.FilVerktoey;
import no.nav.registre.inntekt.utils.ValidationException;

import no.nav.tjenester.aordningen.arbeidsforhold.v1.Arbeidsforhold;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Organisasjon;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Person;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class AltinnInntektService {

    private final AaregService aaregService;
    private final AltinnInntektConsumer altinnInntektConsumer;
    private final DokmotConsumer dokmotConsumer;

    private static final String EIER = "ORKESTRATOREN";
    private static final String TYPE_ORGANISASJON = "Organisasjon";
    private static final String TYPE_PERSON = "Person";
    // TODO: Add enums to Arbeidsforhold-fields

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
            AaregService aaregService,
            AltinnInntektConsumer altinnInntektConsumer,
            DokmotConsumer dokmotConsumer
    ) {
        this.aaregService = aaregService;
        this.altinnInntektConsumer = altinnInntektConsumer;
        this.dokmotConsumer = dokmotConsumer;
    }

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
            var virksomhetsnummer = inntekt.getArbeidsgiver().getVirksomhetsnummer();
            var kontaktinformasjon = hentKontaktinformasjon(virksomhetsnummer, miljoe);
            var nyesteArbeidsforhold = AaregService.finnNyesteArbeidsforholdIOrganisasjon(ident, virksomhetsnummer, arbeidsforholdListe);

            if (Objects.isNull(nyesteArbeidsforhold)) {
                continue;
            }

            var altinnInntektRequest = lagAltinnInntektRequest(inntekt, nyesteArbeidsforhold, kontaktinformasjon, ident);
            var altinnInntektResponse = altinnInntektConsumer.getInntektsmeldingXml201812(altinnInntektRequest);

            altinnInntektMeldinger.add(altinnInntektResponse);

            lagreMeldingIJoark(altinnInntektResponse, inntekt, dollyRequest);
        }
        return altinnInntektMeldinger;
    }

    private RsInntektsmelding lagAltinnInntektRequest(
            RsAltinnInntektInfo inntekt,
            Arbeidsforhold nyesteArbeidsforhold,
            RsKontaktinformasjon kontaktinformasjon,
            String ident
    ) {
        String virksomhetsnummer = "";
        if (nyesteArbeidsforhold.getOpplysningspliktig().getType().equals(TYPE_ORGANISASJON)) {
            virksomhetsnummer = ((Organisasjon)nyesteArbeidsforhold.getOpplysningspliktig()).getOrganisasjonsnummer();
        } else if (nyesteArbeidsforhold.getOpplysningspliktig().getType().equals(TYPE_PERSON)) {
            virksomhetsnummer = ((Person)nyesteArbeidsforhold.getOpplysningspliktig()).getOffentligIdent();
        }

        kontaktinformasjon = Objects.isNull(inntekt.getArbeidsgiver().getKontaktinformasjon()) ? kontaktinformasjon : inntekt.getArbeidsgiver().getKontaktinformasjon();

        var tmp = RsInntektsmelding.builder()
                .ytelse(inntekt.getYtelse().getValue())
                .aarsakTilInnsending(inntekt.getAarsakTilInnsending().getValue())
                .arbeidstakerFnr(ident)
                .naerRelasjon(inntekt.isNaerRelasjon())
                .avsendersystem(inntekt.getAvsendersystem())
                .refusjon(inntekt.getRefusjon())
                .omsorgspenger(inntekt.getOmsorgspenger())
                .sykepengerIArbeidsgiverperioden(inntekt.getSykepengerIArbeidsgiverperioden())
                .startdatoForeldrepengeperiode(inntekt.getStartdatoForeldrepengeperiode())
                .opphoerAvNaturalytelseListe(inntekt.getOpphoerAvNaturalytelseListe().stream().map(
                        m -> RsNaturalytelseDetaljer.builder()
                                .naturalytelseType(m.getNaturalytelseType().getValue())
                                .beloepPrMnd(m.getBeloepPrMnd())
                                .fom(m.getFom()).build())
                        .collect(Collectors.toList()))
                .gjenopptakelseNaturalytelseListe(inntekt.getGjenopptakelseNaturalytelseListe().stream().map(
                        m -> RsNaturalytelseDetaljer.builder()
                                .naturalytelseType(m.getNaturalytelseType().getValue())
                                .beloepPrMnd(m.getBeloepPrMnd())
                                .fom(m.getFom()).build())
                        .collect(Collectors.toList()))
                .pleiepengerPerioder(inntekt.getPleiepengerPerioder());

        if (nyesteArbeidsforhold.getOpplysningspliktig().getType().equals(TYPE_PERSON)) {
            tmp.arbeidsgiverPrivat(RsArbeidsgiverPrivat.builder()
                    .arbeidsgiverFnr(virksomhetsnummer)
                    .kontaktinformasjon(kontaktinformasjon)
                    .build());
        } else {
            tmp.arbeidsgiver(RsArbeidsgiver.builder()
                    .virksomhetsnummer(virksomhetsnummer)
                    .kontaktinformasjon(kontaktinformasjon)
                    .build());
        }

        tmp.arbeidsforhold(RsArbeidsforhold.builder()
                .arbeidsforholdId(nyesteArbeidsforhold.getArbeidsforholdId())
                .beregnetInntekt(RsInntekt.builder()
                        .beloep(inntekt.getArbeidsforhold().getBeregnetInntekt().getBeloep())
                        .aarsakVedEndring(inntekt.getArbeidsforhold().getBeregnetInntekt().getAarsakVedEndring().getValue())
                        .build())
                .avtaltFerieListe(inntekt.getArbeidsforhold().getAvtaltFerieListe())
                .foersteFravaersdag(inntekt.getArbeidsforhold().getFoersteFravaersdag())
                .graderingIForeldrepengerListe(inntekt.getArbeidsforhold().getGraderingIForeldrepengerListe())
                .utsettelseAvForeldrepengerListe(inntekt.getArbeidsforhold().getUtsettelseAvForeldrepengerListe().stream().map(
                        m -> RsUtsettelseAvForeldrepenger.builder()
                                .aarsakTilUtsettelse(m.getAarsakTilUtsettelse().getValue())
                                .periode(m.getPeriode()).build()).collect(Collectors.toList()))
                .build());

        return tmp.build();
    }

    private RsKontaktinformasjon hentKontaktinformasjon(String virksomhetsnummer, String miljoe) {
        // TODO: hent kontaktinformasjon fra ereg
        return RsKontaktinformasjon.builder()
                .kontaktinformasjonNavn("SJÆFEN SJØL")
                .telefonnummer("99999999")
                .build();
    }

    private void lagreMeldingIJoark(String xmlMelding, RsAltinnInntektInfo inntekt, AltinnDollyRequest dollyRequest) {
        var metadata = dollyRequest.getJoarkMetadata();
        if (Objects.isNull(metadata)) {
            metadata = new RsJoarkMetadata();
        }
        var request = DokmotRequest.builder()
                .journalposttype(metadata.getJournalpostType())
                .avsenderMottaker(AvsenderMottaker.builder()
                        .id(inntekt.getArbeidsgiver().getVirksomhetsnummer())
                        .idType(metadata.getAvsenderMottakerIdType())
                        .navn(inntekt.getArbeidsgiver().getKontaktinformasjon().getKontaktinformasjonNavn()).build())
                .bruker(Bruker.builder()
                        .id(dollyRequest.getArbeidstakerFnr())
                        .idType(metadata.getBrukerIdType())
                        .build())
                .tema(metadata.getTema())
                .tittel(metadata.getTittel())
                .kanal(metadata.getKanal())
                .eksternReferanseId(metadata.getEksternReferanseId())
                .datoMottatt(Date.from(inntekt.getAvsendersystem().getInnsendingstidspunkt().atZone(ZoneId.systemDefault()).toInstant()))
                .dokumenter(Collections.singletonList(Dokument.builder()
                        .brevkode(metadata.getBrevkode())
                        .dokumentkategori(metadata.getBrevkategori())
                        .tittel(metadata.getTittel())
                        .dokumentvarianter(Arrays.asList(
                                Dokumentvariant.builder()
                                        .filtype(metadata.getFiltypeOriginal())
                                        .variantformat(metadata.getVariantformatOriginal())
                                        .fysiskDokument(Base64.getEncoder().encode(xmlMelding.getBytes(UTF_8)))
                                        .build(),
                                Dokumentvariant.builder()
                                        .filtype(metadata.getFiltypeArkiv())
                                        .variantformat(metadata.getVariantformatArkiv())
                                        .fysiskDokument(FilVerktoey.encodeFilTilBase64Binary(dummyPdf))
                                        .build())).build())).build();
        dokmotConsumer.opprettJournalpost(request, dollyRequest.getMiljoe());
    }
}
