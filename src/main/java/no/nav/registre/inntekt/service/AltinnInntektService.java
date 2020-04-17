package no.nav.registre.inntekt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import no.nav.registre.inntekt.consumer.rs.AltinnInntektConsumer;
import no.nav.registre.inntekt.consumer.rs.dokmot.DokmotConsumer;
import no.nav.registre.inntekt.domain.altinn.RsAltinnInntektInfo;
import no.nav.registre.inntekt.domain.altinn.enums.AltinnEnum;
import no.nav.registre.inntekt.domain.altinn.rs.RsArbeidsforhold;
import no.nav.registre.inntekt.domain.altinn.rs.RsArbeidsgiver;
import no.nav.registre.inntekt.domain.altinn.rs.RsArbeidsgiverPrivat;
import no.nav.registre.inntekt.domain.altinn.rs.RsInntekt;
import no.nav.registre.inntekt.domain.altinn.rs.RsInntektsmelding;
import no.nav.registre.inntekt.domain.altinn.rs.RsKontaktinformasjon;
import no.nav.registre.inntekt.domain.altinn.rs.RsNaturalytelseDetaljer;
import no.nav.registre.inntekt.domain.altinn.rs.RsUtsettelseAvForeldrepenger;
import no.nav.registre.inntekt.domain.dokmot.InntektDokument;
import no.nav.registre.inntekt.domain.dokmot.ProsessertInntektDokument;
import no.nav.registre.inntekt.domain.dokmot.RsJoarkMetadata;
import no.nav.registre.inntekt.provider.rs.requests.AltinnDollyRequest;
import no.nav.registre.inntekt.utils.ValidationException;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Arbeidsforhold;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Organisasjon;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Person;

@Slf4j
@Service
public class AltinnInntektService {

    private static final String TYPE_ORGANISASJON = "Organisasjon";
    private static final String TYPE_PERSON = "Person";

    private final AaregService aaregService;
    private final AltinnInntektConsumer altinnInntektConsumer;
    private final DokmotConsumer dokmotConsumer;

    public AltinnInntektService(
            AaregService aaregService,
            AltinnInntektConsumer altinnInntektConsumer,
            DokmotConsumer dokmotConsumer
    ) {
        this.aaregService = aaregService;
        this.altinnInntektConsumer = altinnInntektConsumer;
        this.dokmotConsumer = dokmotConsumer;
    }

    private static String getValueFromEnumIfSet(AltinnEnum altinnEnum) {
        return Objects.isNull(altinnEnum) ? null : altinnEnum.getValue();
    }

    public List<ProsessertInntektDokument> lagAltinnMeldinger(AltinnDollyRequest dollyRequest, Boolean continueOnError) throws ValidationException {
        var miljoe = dollyRequest.getMiljoe();
        var ident = dollyRequest.getArbeidstakerFnr();
        var inntekterAaOpprette = dollyRequest.getInntekter();
        RsJoarkMetadata metadata = dollyRequest.getJoarkMetadata() != null
                ? dollyRequest.getJoarkMetadata()
                : new RsJoarkMetadata();

        var arbeidsforholdListe = aaregService.hentArbeidsforhold(ident, miljoe);
        if (arbeidsforholdListe == null || arbeidsforholdListe.isEmpty()) {
            log.error("Kunne ikke finne arbeidsforhold for ident");
            throw new ValidationException("Kunne ikke finne arbeidsforhold for ident " + ident);
        }

        // TODO: Refaktorering -- dersom én av virksomhetsnumrene feiler, kutter alle. Beholder for MVP
        var altinnInntektMeldinger = new ArrayList<String>(inntekterAaOpprette.size());


        ArrayList<InntektDokument> inntektDokuments = new ArrayList<>();

        inntekterAaOpprette.forEach(inntekt -> {
            var virksomhetsnummer = inntekt.getArbeidsgiver().getVirksomhetsnummer();
            var kontaktinformasjon = hentKontaktinformasjon(virksomhetsnummer, miljoe);
            Arbeidsforhold nyesteArbeidsforhold = null;

            try {
                nyesteArbeidsforhold = AaregService.finnNyesteArbeidsforholdIOrganisasjon(ident, virksomhetsnummer, arbeidsforholdListe);
            } catch (ValidationException e) {
                log.error("Fant ikke nyeste arbeidsforhold for {}", virksomhetsnummer, e);
                return;
            }

            var request = lagAltinnInntektRequest(inntekt, nyesteArbeidsforhold, kontaktinformasjon, ident);
            var response = altinnInntektConsumer.getInntektsmeldingXml201812(request, continueOnError);
            altinnInntektMeldinger.add(response);
            inntektDokuments.add(
                    InntektDokument
                            .builder()
                            .arbeidstakerFnr(dollyRequest.getArbeidstakerFnr())
                            .metadata(metadata)
                            .datoMottatt(Date.from(inntekt.getAvsendersystem().getInnsendingstidspunkt().atZone(ZoneId.systemDefault()).toInstant()))
                            .virksomhetsnavn(inntekt.getArbeidsgiver().getKontaktinformasjon().getKontaktinformasjonNavn())
                            .virksomhetsnummer(inntekt.getArbeidsgiver().getVirksomhetsnummer())
                            .xml(response)
                            .build()
            );
        });
        if(!continueOnError & inntektDokuments.size()!=inntekterAaOpprette.size()){
            throw new ValidationException("Fant ikke nyeste arbeidsforhold for alle virksomhetsnummer");
        }

        return dokmotConsumer.opprettJournalpost(dollyRequest.getMiljoe(), inntektDokuments);
    }

    private RsInntektsmelding lagAltinnInntektRequest(
            RsAltinnInntektInfo inntekt,
            Arbeidsforhold nyesteArbeidsforhold,
            RsKontaktinformasjon kontaktinformasjon,
            String ident
    ) {
        String virksomhetsnummer = "";
        if (nyesteArbeidsforhold.getArbeidsgiver().getType().equals(TYPE_ORGANISASJON)) {
            virksomhetsnummer = ((Organisasjon) nyesteArbeidsforhold.getArbeidsgiver()).getOrganisasjonsnummer();
        } else if (nyesteArbeidsforhold.getArbeidsgiver().getType().equals(TYPE_PERSON)) {
            virksomhetsnummer = ((Person) nyesteArbeidsforhold.getArbeidsgiver()).getOffentligIdent();
        }

        kontaktinformasjon = Objects.isNull(inntekt.getArbeidsgiver().getKontaktinformasjon()) ? kontaktinformasjon : inntekt.getArbeidsgiver().getKontaktinformasjon();

        var tmp = RsInntektsmelding.builder()
                .ytelse(getValueFromEnumIfSet(inntekt.getYtelse()))
                .aarsakTilInnsending(getValueFromEnumIfSet(inntekt.getAarsakTilInnsending()))
                .arbeidstakerFnr(ident)
                .naerRelasjon(inntekt.isNaerRelasjon())
                .avsendersystem(inntekt.getAvsendersystem())
                .refusjon(inntekt.getRefusjon())
                .omsorgspenger(inntekt.getOmsorgspenger())
                .sykepengerIArbeidsgiverperioden(inntekt.getSykepengerIArbeidsgiverperioden())
                .startdatoForeldrepengeperiode(inntekt.getStartdatoForeldrepengeperiode())
                .opphoerAvNaturalytelseListe(inntekt.getOpphoerAvNaturalytelseListe().stream().map(
                        m -> RsNaturalytelseDetaljer.builder()
                                .naturalytelseType(getValueFromEnumIfSet(m.getNaturalytelseType()))
                                .beloepPrMnd(m.getBeloepPrMnd())
                                .fom(m.getFom()).build())
                        .collect(Collectors.toList()))
                .gjenopptakelseNaturalytelseListe(inntekt.getGjenopptakelseNaturalytelseListe().stream().map(
                        m -> RsNaturalytelseDetaljer.builder()
                                .naturalytelseType(getValueFromEnumIfSet(m.getNaturalytelseType()))
                                .beloepPrMnd(m.getBeloepPrMnd())
                                .fom(m.getFom()).build())
                        .collect(Collectors.toList()))
                .pleiepengerPerioder(inntekt.getPleiepengerPerioder());

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

        //TODO (OL): Legge til arbeidsforholdId når dette settes tilsvarende i AA-Reg
        tmp.arbeidsforhold(RsArbeidsforhold.builder()
                .beregnetInntekt(RsInntekt.builder()
                        .beloep(inntekt.getArbeidsforhold().getBeregnetInntekt().getBeloep())
                        .aarsakVedEndring(getValueFromEnumIfSet(inntekt.getArbeidsforhold().getBeregnetInntekt().getAarsakVedEndring()))
                        .build())
                .avtaltFerieListe(inntekt.getArbeidsforhold().getAvtaltFerieListe())
                .foersteFravaersdag(inntekt.getArbeidsforhold().getFoersteFravaersdag())
                .graderingIForeldrepengerListe(inntekt.getArbeidsforhold().getGraderingIForeldrepengerListe())
                .utsettelseAvForeldrepengerListe(inntekt.getArbeidsforhold().getUtsettelseAvForeldrepengerListe().stream().map(
                        m -> RsUtsettelseAvForeldrepenger.builder()
                                .aarsakTilUtsettelse(getValueFromEnumIfSet(m.getAarsakTilUtsettelse()))
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
}
