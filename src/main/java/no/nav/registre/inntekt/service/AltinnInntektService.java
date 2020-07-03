package no.nav.registre.inntekt.service;

import static no.nav.registre.inntekt.utils.CommonConstants.TYPE_ORGANISASJON;
import static no.nav.registre.inntekt.utils.CommonConstants.TYPE_PERSON;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.AltinnInntektConsumer;
import no.nav.registre.inntekt.consumer.rs.dokmot.DokmotConsumer;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.RsInntektsmeldingRequest;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.enums.AltinnEnum;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsArbeidsforhold;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsArbeidsgiver;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsArbeidsgiverPrivat;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsInntekt;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsInntektsmelding;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsKontaktinformasjon;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsNaturalytelseDetaljer;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsUtsettelseAvForeldrepenger;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.InntektDokument;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.ProsessertInntektDokument;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.RsJoarkMetadata;
import no.nav.registre.inntekt.provider.rs.requests.AltinnInntektsmeldingRequest;
import no.nav.registre.inntekt.utils.ArbeidsforholdMappingUtil;
import no.nav.registre.inntekt.utils.ValidationException;
import no.nav.registre.testnorge.domain.dto.aordningen.arbeidsforhold.Arbeidsforhold;
import no.nav.registre.testnorge.domain.dto.aordningen.arbeidsforhold.Organisasjon;
import no.nav.registre.testnorge.domain.dto.aordningen.arbeidsforhold.Person;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AltinnInntektService {

    private final AaregService aaregService;
    private final AltinnInntektConsumer altinnInntektConsumer;
    private final DokmotConsumer dokmotConsumer;

    private static String getValueFromEnumIfSet(AltinnEnum altinnEnum) {
        return Objects.isNull(altinnEnum) ? null : altinnEnum.getValue();
    }

    public List<ProsessertInntektDokument> lagAltinnMeldinger(
            AltinnInntektsmeldingRequest dollyRequest,
            Boolean continueOnError,
            Boolean valider,
            Boolean includeXml
    ) throws ValidationException {
        // valider input?
        var inntektDokuments = (valider == null || valider)
                ? lagInntektDokumenter(dollyRequest, continueOnError)
                : lagInntektDokumenter(dollyRequest);
        // lag altinn meldinger

        // send altinnmeldingene
        var inntektDokumentStatuser = dokmotConsumer.opprettJournalpost(dollyRequest.getMiljoe(), inntektDokuments);
        // generer returnerbar "rapport"

    }

    // lag altinn meldinger genererer altinn XML ved kall til altinninntektstub.
    public List<String> genererAltinnMeldinger(
            List<RsInntektsmeldingRequest> inntekter,
            String arbeidstakerFnr,
            boolean validerArbeidsforhold
    ) {
        return new ArrayList<>();
    }

    public String genererAltinnMelding(
            RsInntektsmeldingRequest inntekt,
            String arbeidstakerFnr,
            boolean validerArbeidsforhold,
            String miljoe
    ) throws ValidationException {
        var arbeidsgiverId = finnArbeidsgiverId(inntekt);
        var arbeidsforhold = (validerArbeidsforhold)
                ? hentArbeidsforhold(arbeidstakerFnr, arbeidsgiverId, miljoe)
                : inntekt.getArbeidsforhold();
        var kontaktinformasjon = hentKontaktinformasjonFraEreg(arbeidsgiverId, miljoe);
    }

    private String finnArbeidsgiverId(RsInntektsmeldingRequest inntekt) throws ValidationException {
        if (inntekt.getArbeidsgiver() != null) {
            return inntekt.getArbeidsgiver().getVirksomhetsnummer();
        } else if (inntekt.getArbeidsgiverPrivat() != null) {
            return inntekt.getArbeidsgiverPrivat().getArbeidsgiverFnr();
        }
        throw new ValidationException("Ingen arbeidsgiverId ble funnet for inntekten.");
    }

    // send altinnMeldinger sender xml-meldinger til joark i miljø

    // generer rapport

    private RsArbeidsforhold hentArbeidsforhold(
            String arbeidstakerFnr,
            String arbeidsgiverId,
            String miljoe
    ) throws ValidationException {

        var arbeidsforholdListe = aaregService.hentArbeidsforhold(arbeidstakerFnr, miljoe);
        if (arbeidsforholdListe == null || arbeidsforholdListe.isEmpty()) {
            log.error("Kunne ikke finne arbeidsforhold for ident");
            throw new ValidationException("Kunne ikke finne arbeidsforhold for ident " + arbeidstakerFnr);
        }

        try {
            var nyesteAaregArbeidsforhold = AaregService.finnNyesteArbeidsforholdIOrganisasjon(
                    arbeidstakerFnr, arbeidsgiverId, arbeidsforholdListe);
            return ArbeidsforholdMappingUtil.mapArbeidsforholdToRsArbeidsforhold(nyesteArbeidsforhold);


        } catch (ValidationException e) {
            log.error("Fant ikke nyeste arbeidsforhold for {}", arbeidsgiverId, e);
            throw new ValidationException("Fant ikke nyeste arbeidsforhold");
        }
    }

    private List<InntektDokument> lagInntektDokumenter(
            AltinnInntektsmeldingRequest dollyRequest,
            Boolean continueOnError
    ) throws ValidationException {

        var miljoe = dollyRequest.getMiljoe();
        var ident = dollyRequest.getArbeidstakerFnr();
        var inntekterAaOpprette = dollyRequest.getInntekter();
        var metadata = (dollyRequest.getJoarkMetadata() != null)
                ? dollyRequest.getJoarkMetadata()
                : new RsJoarkMetadata();
        var inntektDokumenter = new ArrayList<InntektDokument>(inntekterAaOpprette.size());

        /////////// starter validering ////////////////
        var arbeidsforholdListe = aaregService.hentArbeidsforhold(ident, miljoe);
        if (arbeidsforholdListe == null || arbeidsforholdListe.isEmpty()) {
            log.error("Kunne ikke finne arbeidsforhold for ident");
            throw new ValidationException("Kunne ikke finne arbeidsforhold for ident " + ident);
        }

        inntekterAaOpprette.forEach(inntekt -> {
            String arbeidsgiverId;
            if (inntekt.getArbeidsgiver() != null) {
                arbeidsgiverId = inntekt.getArbeidsgiver().getVirksomhetsnummer();
            } else if (inntekt.getArbeidsgiverPrivat() != null) {
                arbeidsgiverId = inntekt.getArbeidsgiverPrivat().getArbeidsgiverFnr();
            } else {
                throw new RuntimeException("Må legge ved arbeidsgiver for å kunne opprette inntekt.");
            }
            var kontaktinformasjon = hentKontaktinformasjonFraEreg(arbeidsgiverId, miljoe);
            Arbeidsforhold nyesteArbeidsforhold;

            try {
                nyesteArbeidsforhold = AaregService.finnNyesteArbeidsforholdIOrganisasjon(ident, arbeidsgiverId, arbeidsforholdListe);
            } catch (ValidationException e) {
                log.error("Fant ikke nyeste arbeidsforhold for {}", arbeidsgiverId, e);
                return;
            }

            var request = lagAltinnInntektRequest(inntekt, nyesteArbeidsforhold, kontaktinformasjon, ident);
            var response = altinnInntektConsumer.getInntektsmeldingXml201812(request, continueOnError);
            inntektDokumenter.add(
                    InntektDokument
                            .builder()
                            .arbeidstakerFnr(dollyRequest.getArbeidstakerFnr())
                            .metadata(metadata)
                            .datoMottatt(Date.from(inntekt.getAvsendersystem().getInnsendingstidspunkt().atZone(ZoneId.systemDefault()).toInstant()))
                            .virksomhetsnavn(inntekt.getArbeidsgiver().getKontaktinformasjon().getKontaktinformasjonNavn())
                            .virksomhetsnummer(arbeidsgiverId)
                            .xml(response)
                            .build()
            );
        });
        if (!continueOnError && (inntektDokumenter.size() != inntekterAaOpprette.size())) {
            throw new ValidationException("Fant ikke nyeste arbeidsforhold for alle virksomhetsnummer");
        }
        return inntektDokumenter;
    }

    private List<InntektDokument> lagInntektDokumenter(AltinnInntektsmeldingRequest dollyRequest) {
        var ident = dollyRequest.getArbeidstakerFnr();
        var inntekterAaOpprette = dollyRequest.getInntekter();
        var metadata = dollyRequest.getJoarkMetadata() != null
                ? dollyRequest.getJoarkMetadata()
                : new RsJoarkMetadata();

        var inntektDokumenter = new ArrayList<InntektDokument>(inntekterAaOpprette.size());

        inntekterAaOpprette.forEach(inntekt -> {
            try {
                var rsInntektsmelding = lagAltinnInntektRequest(inntekt, ident);
                var respons = altinnInntektConsumer.getInntektsmeldingXml201812(rsInntektsmelding, true);

                var arbeidsgiverId = getArbeidsgiverId(inntekt);
                var kontaktinformasjon = getKontaktinformasjon(inntekt);

                inntektDokumenter.add(InntektDokument.builder()
                        .arbeidstakerFnr(rsInntektsmelding.getArbeidstakerFnr())
                        .metadata(metadata)
                        .datoMottatt(Date.from(rsInntektsmelding.getAvsendersystem().getInnsendingstidspunkt().atZone(ZoneId.systemDefault()).toInstant()))
                        .virksomhetsnavn(kontaktinformasjon.getKontaktinformasjonNavn())
                        .virksomhetsnummer(arbeidsgiverId)
                        .xml(respons)
                        .build());
            } catch (ValidationException ignore) {
            }
        });

        return inntektDokumenter;
    }

    private RsInntektsmelding lagAltinnInntektRequest(
            RsInntektsmeldingRequest inntekt,
            String ident
    ) throws ValidationException {
        var tmp = mapAltinnInntektInfoTilRsInntektsmelding(ident, inntekt);

        var arbeidsgiverId = getArbeidsgiverId(inntekt);
        var kontaktinformasjon = getKontaktinformasjon(inntekt);

        if (kontaktinformasjon == null) {
            throw new ValidationException("Må legge ved kontaktinformasjon for arbeidsgiver.");
        }

        if (inntekt.getArbeidsgiver() != null) {
            tmp.arbeidsgiver(RsArbeidsgiver.builder()
                    .kontaktinformasjon(RsKontaktinformasjon.builder()
                            .telefonnummer(kontaktinformasjon.getTelefonnummer())
                            .kontaktinformasjonNavn(kontaktinformasjon.getKontaktinformasjonNavn())
                            .build())
                    .virksomhetsnummer(arbeidsgiverId)
                    .build());
        } else {
            tmp.arbeidsgiverPrivat(RsArbeidsgiverPrivat.builder()
                    .kontaktinformasjon(RsKontaktinformasjon.builder()
                            .telefonnummer(kontaktinformasjon.getTelefonnummer())
                            .kontaktinformasjonNavn(kontaktinformasjon.getKontaktinformasjonNavn())
                            .build())
                    .arbeidsgiverFnr(arbeidsgiverId)
                    .build());
        }
        return tmp.build();
    }

    private RsInntektsmelding lagAltinnInntektRequest(
            RsInntektsmeldingRequest inntekt,
            Arbeidsforhold nyesteArbeidsforhold,
            RsKontaktinformasjon kontaktinformasjon,
            String ident
    ) {
        var virksomhetsnummer = "";
        if (nyesteArbeidsforhold.getArbeidsgiver().getType().equals(TYPE_ORGANISASJON)) {
            virksomhetsnummer = ((Organisasjon) nyesteArbeidsforhold.getArbeidsgiver()).getOrganisasjonsnummer();
        } else if (nyesteArbeidsforhold.getArbeidsgiver().getType().equals(TYPE_PERSON)) {
            virksomhetsnummer = ((Person) nyesteArbeidsforhold.getArbeidsgiver()).getOffentligIdent();
        }

        kontaktinformasjon = (Objects.isNull(inntekt.getArbeidsgiver().getKontaktinformasjon()))
                ? kontaktinformasjon
                : inntekt.getArbeidsgiver().getKontaktinformasjon();

        var tmp = mapAltinnInntektInfoTilRsInntektsmelding(ident, inntekt);

        if (nyesteArbeidsforhold.getArbeidsgiver().getType().equals(TYPE_PERSON)) {
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

        return tmp.build();
    }

    private RsInntektsmelding.RsInntektsmeldingBuilder mapAltinnInntektInfoTilRsInntektsmelding(
            String ident,
            RsInntektsmeldingRequest inntekt
    ) {
        return RsInntektsmelding.builder()
                .ytelse(inntekt.getYtelse())
                .aarsakTilInnsending(inntekt.getAarsakTilInnsending())
                .arbeidstakerFnr(ident)
                .naerRelasjon(inntekt.isNaerRelasjon())
                .avsendersystem(inntekt.getAvsendersystem())
                .refusjon(inntekt.getRefusjon())
                .omsorgspenger(inntekt.getOmsorgspenger())
                .sykepengerIArbeidsgiverperioden(inntekt.getSykepengerIArbeidsgiverperioden())
                .startdatoForeldrepengeperiode(inntekt.getStartdatoForeldrepengeperiode())
                .opphoerAvNaturalytelseListe(inntekt.getOpphoerAvNaturalytelseListe().stream().map(
                        m -> RsNaturalytelseDetaljer.builder()
                                .naturalytelseType(m.getNaturalytelseType())
                                .beloepPrMnd(m.getBeloepPrMnd())
                                .fom(m.getFom()).build())
                        .collect(Collectors.toList()))
                .gjenopptakelseNaturalytelseListe(inntekt.getGjenopptakelseNaturalytelseListe().stream().map(
                        m -> RsNaturalytelseDetaljer.builder()
                                .naturalytelseType(m.getNaturalytelseType())
                                .beloepPrMnd(m.getBeloepPrMnd())
                                .fom(m.getFom()).build())
                        .collect(Collectors.toList()))
                .pleiepengerPerioder(inntekt.getPleiepengerPerioder())
                .arbeidsforhold(RsArbeidsforhold.builder()
                        .arbeidsforholdId(inntekt.getArbeidsforhold().getArbeidsforholdId())
                        .beregnetInntekt(RsInntekt.builder()
                                .beloep(inntekt.getArbeidsforhold().getBeregnetInntekt().getBeloep())
                                .aarsakVedEndring(inntekt.getArbeidsforhold().getBeregnetInntekt().getAarsakVedEndring())
                                .build())
                        .avtaltFerieListe(inntekt.getArbeidsforhold().getAvtaltFerieListe())
                        .foersteFravaersdag(inntekt.getArbeidsforhold().getFoersteFravaersdag())
                        .graderingIForeldrepengerListe(inntekt.getArbeidsforhold().getGraderingIForeldrepengerListe())
                        .utsettelseAvForeldrepengerListe(inntekt.getArbeidsforhold().getUtsettelseAvForeldrepengerListe().stream().map(
                                m -> RsUtsettelseAvForeldrepenger.builder()
                                        .aarsakTilUtsettelse(m.getAarsakTilUtsettelse())
                                        .periode(m.getPeriode()).build()).collect(Collectors.toList()))
                        .build());
    }

    private String getArbeidsgiverId(RsInntektsmeldingRequest inntekt) throws ValidationException {
        if (inntekt.getArbeidsgiver() != null) {
            return inntekt.getArbeidsgiver().getVirksomhetsnummer();
        } else if (inntekt.getArbeidsgiverPrivat() != null) {
            return inntekt.getArbeidsgiverPrivat().getArbeidsgiverFnr();
        } else {
            throw new ValidationException("Må legge ved arbeidsgiver for å kunne opprette inntekt.");
        }
    }

    private RsKontaktinformasjon getKontaktinformasjon(RsInntektsmeldingRequest inntekt) throws ValidationException {
        if (inntekt.getArbeidsgiver() != null) {
            return inntekt.getArbeidsgiver().getKontaktinformasjon();
        } else if (inntekt.getArbeidsgiverPrivat() != null) {
            return inntekt.getArbeidsgiverPrivat().getKontaktinformasjon();
        } else {
            throw new ValidationException("Må legge ved arbeidsgiver for å kunne opprette inntekt.");
        }
    }

    private RsKontaktinformasjon hentKontaktinformasjonFraEreg(
            String virksomhetsnummer,
            String miljoe
    ) {
        // TODO: hent kontaktinformasjon fra ereg
        return RsKontaktinformasjon.builder()
                .kontaktinformasjonNavn("SJÆFEN SJØL")
                .telefonnummer("99999999")
                .build();
    }
}
