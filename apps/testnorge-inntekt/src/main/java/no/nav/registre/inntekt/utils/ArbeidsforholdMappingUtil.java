package no.nav.registre.inntekt.utils;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.registre.inntekt.domain.aareg.RsAktoer;
import no.nav.registre.inntekt.domain.aareg.RsAktoerPerson;
import no.nav.registre.inntekt.domain.aareg.RsAntallTimerForTimeloennet;
import no.nav.registre.inntekt.domain.aareg.RsArbeidsavtale;
import no.nav.registre.inntekt.domain.aareg.RsArbeidsforhold;
import no.nav.registre.inntekt.domain.aareg.RsOrganisasjon;
import no.nav.registre.inntekt.domain.aareg.RsPeriode;
import no.nav.registre.inntekt.domain.aareg.RsPermisjon;
import no.nav.registre.inntekt.domain.aareg.RsPersonAareg;
import no.nav.registre.inntekt.domain.aareg.RsUtenlandsopphold;
import no.nav.registre.inntekt.exception.UgyldigArbeidsforholdException;
import no.nav.registre.inntekt.exception.UgyldigArbeidsgiverException;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Ansettelsesperiode;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.AntallTimerForTimeloennet;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Arbeidsavtale;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Bruksperiode;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Gyldighetsperiode;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.OpplysningspliktigArbeidsgiver;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Organisasjon;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Periode;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.PermisjonPermittering;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Person;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Sporingsinformasjon;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Utenlandsopphold;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArbeidsforholdMappingUtil {

    private ArbeidsforholdMappingUtil() {

    }

    private static final String SPORINGSINFO = "sporingsinformasjon";
    private static final String PERIODE = "periode";

    public static Arbeidsforhold mapToArbeidsforhold(JsonNode arbeidsforholdNode) {
        return Arbeidsforhold.builder()
                .navArbeidsforholdId(findLongNullSafe(arbeidsforholdNode, "navArbeidsforholdId"))
                .arbeidsforholdId(findStringNullSafe(arbeidsforholdNode, "arbeidsforholdId"))
                .arbeidstaker(mapArbeidstaker(arbeidsforholdNode.get("arbeidstaker")))
                .arbeidsgiver(mapArbeidsgiver(arbeidsforholdNode.get("arbeidsgiver")))
                .opplysningspliktig(mapOpplysningspliktig(arbeidsforholdNode.get("opplysningspliktig")))
                .type(findStringNullSafe(arbeidsforholdNode, "type"))
                .ansettelsesperiode(mapAnsettelsesperiode(arbeidsforholdNode.get("ansettelsesperiode")))
                .arbeidsavtaler(mapArbeidsavtaler(arbeidsforholdNode.get("arbeidsavtaler")))
                .permisjonPermitteringer(mapPermisjonPermitteringer(arbeidsforholdNode.get("permisjonPermitteringer")))
                .antallTimerForTimeloennet(mapAntallTimerForTimeloennet(arbeidsforholdNode.get("antallTimerForTimeloennet")))
                .utenlandsopphold(mapUtenlandsopphold(arbeidsforholdNode.get("utenlandsopphold")))
                .innrapportertEtterAOrdningen(arbeidsforholdNode.get("innrapportertEtterAOrdningen") != null ? arbeidsforholdNode.get("innrapportertEtterAOrdningen").asBoolean() : null)
                .registrert(findLocalDateTimeNullSafe(arbeidsforholdNode, "registrert"))
                .sistBekreftet(findLocalDateTimeNullSafe(arbeidsforholdNode, "sistBekreftet"))
                .sporingsinformasjon(mapSporingsinformasjon(arbeidsforholdNode.get(SPORINGSINFO)))
                .build();
    }

    public static RsArbeidsforhold mapArbeidsforholdToRsArbeidsforhold(Arbeidsforhold arbeidsforhold) {
        return RsArbeidsforhold.builder()
                .arbeidsforholdIDnav(arbeidsforhold.getNavArbeidsforholdId())
                .arbeidsforholdID(arbeidsforhold.getArbeidsforholdId())
                .arbeidstaker(mapRsPersonAareg(arbeidsforhold.getArbeidstaker()))
                .arbeidsgiver(mapRsAktoer(arbeidsforhold.getArbeidsgiver()))
                .arbeidsforholdstype(arbeidsforhold.getType())
                .ansettelsesPeriode(mapRsAnsettelsesPeriode(arbeidsforhold.getAnsettelsesperiode()))
                .arbeidsavtale(mapRsArbeidsavtale(arbeidsforhold.getArbeidsavtaler()))
                .permisjon(mapRsPermisjoner(arbeidsforhold.getPermisjonPermitteringer()))
                .antallTimerForTimeloennet(mapRsAntallTimerForTimeloennet(arbeidsforhold.getAntallTimerForTimeloennet()))
                .utenlandsopphold(mapRsUtenlandsopphold(arbeidsforhold.getUtenlandsopphold()))
                .build();
    }

    public static LocalDateTime getLocalDateTimeFromLocalDate(LocalDate localDate) {
        return localDate != null ? localDate.atStartOfDay() : null;
    }

    private static Person mapArbeidstaker(JsonNode person) {
        return Person.builder()
                .aktoerId(findStringNullSafe(person, "aktoerId"))
                .offentligIdent(findStringNullSafe(person, "offentligIdent"))
                .build();
    }

    private static OpplysningspliktigArbeidsgiver mapArbeidsgiver(JsonNode arbeidsgiver) {
        return getOpplysningspliktigArbeidsgiver(arbeidsgiver);
    }

    private static OpplysningspliktigArbeidsgiver mapOpplysningspliktig(JsonNode opplysningspliktig) {
        return getOpplysningspliktigArbeidsgiver(opplysningspliktig);
    }

    private static OpplysningspliktigArbeidsgiver getOpplysningspliktigArbeidsgiver(JsonNode arbeidsgiver) {
        String arbeidsgiverType = findStringNullSafe(arbeidsgiver, "type");
        OpplysningspliktigArbeidsgiver opplysningspliktigArbeidsgiver;
        if ("Organisasjon".equals(arbeidsgiverType)) {
            opplysningspliktigArbeidsgiver = Organisasjon.builder()
                    .organisasjonsnummer(findStringNullSafe(arbeidsgiver, "organisasjonsnummer"))
                    .build();
        } else if ("Person".equals(arbeidsgiverType)) {
            opplysningspliktigArbeidsgiver = Person.builder()
                    .aktoerId(findStringNullSafe(arbeidsgiver, "aktoerId"))
                    .offentligIdent(findStringNullSafe(arbeidsgiver, "offentligIdent"))
                    .build();
        } else {
            throw new UgyldigArbeidsgiverException("Ukjent arbeidsgivertype: " + arbeidsgiverType);
        }
        return opplysningspliktigArbeidsgiver;
    }

    private static Ansettelsesperiode mapAnsettelsesperiode(JsonNode ansettelsesperiode) {
        return Ansettelsesperiode.builder()
                .periode(mapPeriode(ansettelsesperiode.get(PERIODE)))
                .varslingskode(findStringNullSafe(ansettelsesperiode, "varslingskode"))
                .bruksperiode(mapBruksperiode(ansettelsesperiode.get("bruksperiode")))
                .sporingsinformasjon(mapSporingsinformasjon(ansettelsesperiode.get(SPORINGSINFO)))
                .build();
    }

    private static Periode mapPeriode(JsonNode periode) {
        return Periode.builder()
                .fom(findLocalDateNullSafe(periode, "fom"))
                .tom(findLocalDateNullSafe(periode, "tom"))
                .build();
    }

    private static Bruksperiode mapBruksperiode(JsonNode bruksperiode) {
        return Bruksperiode.builder()
                .fom(findLocalDateTimeNullSafe(bruksperiode, "fom"))
                .tom(findLocalDateTimeNullSafe(bruksperiode, "tom"))
                .build();
    }

    private static Gyldighetsperiode mapGyldighetsperiode(JsonNode gyldighetsperiode) {
        return Gyldighetsperiode.builder()
                .fom(findLocalDateNullSafe(gyldighetsperiode, "fom"))
                .tom(findLocalDateNullSafe(gyldighetsperiode, "tom"))
                .build();
    }

    private static List<Arbeidsavtale> mapArbeidsavtaler(JsonNode arbeidsavtaler) {
        List<Arbeidsavtale> arbeidsavtaleListe = new ArrayList<>();
        for (var arbeidsavtale : arbeidsavtaler) {
            arbeidsavtaleListe.add(mapArbeidsavtale(arbeidsavtale));
        }
        return arbeidsavtaleListe;
    }

    private static Arbeidsavtale mapArbeidsavtale(JsonNode arbeidsavtale) {
        return Arbeidsavtale.builder()
                .arbeidstidsordning(findStringNullSafe(arbeidsavtale, "arbeidstidsordning"))
                .yrke(findStringNullSafe(arbeidsavtale, "yrke"))
                .stillingsprosent(findDoubleNullSafe(arbeidsavtale, "stillingsprosent"))
                .antallTimerPrUke(findDoubleNullSafe(arbeidsavtale, "antallTimerPrUke"))
                .beregnetAntallTimerPrUke(findDoubleNullSafe(arbeidsavtale, "beregnetAntallTimerPrUke"))
                .sistLoennsendring(findLocalDateNullSafe(arbeidsavtale, "sistLoennsendring"))
                .sistStillingsendring(findLocalDateNullSafe(arbeidsavtale, "sistStillingsendring"))
                .bruksperiode(mapBruksperiode(arbeidsavtale.get("bruksperiode")))
                .gyldighetsperiode(mapGyldighetsperiode(arbeidsavtale.get("gyldighetsperiode")))
                .sporingsinformasjon(mapSporingsinformasjon(arbeidsavtale.get(SPORINGSINFO)))
                .build();
    }

    private static List<PermisjonPermittering> mapPermisjonPermitteringer(JsonNode permisjonPermitteringer) {
        if (permisjonPermitteringer == null) {
            return Collections.emptyList();
        }
        List<PermisjonPermittering> permisjonPermitteringListe = new ArrayList<>();
        for (var permisjonPermittering : permisjonPermitteringer) {
            permisjonPermitteringListe.add(PermisjonPermittering.builder()
                    .permisjonPermitteringId(findStringNullSafe(permisjonPermittering, "permisjonPermitteringId"))
                    .periode(mapPeriode(permisjonPermittering.get(PERIODE)))
                    .prosent(findDoubleNullSafe(permisjonPermittering, "prosent"))
                    .type(findStringNullSafe(permisjonPermittering, "type"))
                    .varslingskode(findStringNullSafe(permisjonPermittering, "varslingskode"))
                    .sporingsinformasjon(mapSporingsinformasjon(permisjonPermittering.get(SPORINGSINFO)))
                    .build());
        }
        return permisjonPermitteringListe;
    }

    private static List<AntallTimerForTimeloennet> mapAntallTimerForTimeloennet(JsonNode antallTimerForTimeloennet) {
        if (antallTimerForTimeloennet == null) {
            return Collections.emptyList();
        }
        List<AntallTimerForTimeloennet> antallTimerForTimeloennetListe = new ArrayList<>();
        for (var antallTimer : antallTimerForTimeloennet) {
            antallTimerForTimeloennetListe.add(AntallTimerForTimeloennet.builder()
                    .periode(mapPeriode(antallTimer.get(PERIODE)))
                    .antallTimer(findDoubleNullSafe(antallTimer, "antallTimer"))
                    .rapporteringsperiode(findYearMonthNullSage(antallTimer, "rapporteringsperiode"))
                    .sporingsinformasjon(mapSporingsinformasjon(antallTimer.get(SPORINGSINFO)))
                    .build());
        }
        return antallTimerForTimeloennetListe;
    }

    private static List<Utenlandsopphold> mapUtenlandsopphold(JsonNode utenlandsopphold) {
        if (utenlandsopphold == null) {
            return Collections.emptyList();
        }
        List<Utenlandsopphold> utenlandsoppholdListe = new ArrayList<>();
        for (var opphold : utenlandsopphold) {
            utenlandsoppholdListe.add(Utenlandsopphold.builder()
                    .periode(mapPeriode(opphold.get(PERIODE)))
                    .landkode(findStringNullSafe(opphold, "landkode"))
                    .rapporteringsperiode(findYearMonthNullSage(opphold, "rapporteringsperiode"))
                    .sporingsinformasjon(mapSporingsinformasjon(opphold.get(SPORINGSINFO)))
                    .build());
        }
        return utenlandsoppholdListe;
    }

    private static Sporingsinformasjon mapSporingsinformasjon(JsonNode sporingsinformasjon) {
        return Sporingsinformasjon.builder()
                .opprettetTidspunkt(findLocalDateTimeNullSafe(sporingsinformasjon, "opprettetTidspunkt"))
                .opprettetAv(findStringNullSafe(sporingsinformasjon, "opprettetAv"))
                .opprettetKilde(findStringNullSafe(sporingsinformasjon, "opprettetKilde"))
                .opprettetKildereferanse(findStringNullSafe(sporingsinformasjon, "opprettetKildereferanse"))
                .endretTidspunkt(findLocalDateTimeNullSafe(sporingsinformasjon, "endretTidspunkt"))
                .endretAv(findStringNullSafe(sporingsinformasjon, "endretAv"))
                .endretKilde(findStringNullSafe(sporingsinformasjon, "endretKilde"))
                .endretKildereferanse(findStringNullSafe(sporingsinformasjon, "endretKildereferanse"))
                .build();
    }

    private static RsPersonAareg mapRsPersonAareg(Person person) {
        return RsPersonAareg.builder()
                .ident(person.getOffentligIdent())
                .build();
    }

    private static RsAktoer mapRsAktoer(OpplysningspliktigArbeidsgiver opplysningspliktigArbeidsgiver) {
        if ("Organisasjon".equals(opplysningspliktigArbeidsgiver.getType())) {
            var rsOrganisasjon = RsOrganisasjon.builder()
                    .orgnummer(((Organisasjon) opplysningspliktigArbeidsgiver).getOrganisasjonsnummer())
                    .build();
            rsOrganisasjon.setAktoertype("ORG");
            return rsOrganisasjon;
        } else if ("Person".equals(opplysningspliktigArbeidsgiver.getType())) {
            var rsAktoerPerson = RsAktoerPerson.builder()
                    .ident(((Person) opplysningspliktigArbeidsgiver).getOffentligIdent())
                    .build();
            rsAktoerPerson.setAktoertype("PERS");
            return rsAktoerPerson;
        } else {
            throw new UgyldigArbeidsforholdException("Ukjent aktørtype: " + opplysningspliktigArbeidsgiver.getType());
        }
    }

    private static RsPeriode mapRsAnsettelsesPeriode(Ansettelsesperiode ansettelsesperiode) {
        return RsPeriode.builder()
                .fom(getLocalDateTimeFromLocalDate(ansettelsesperiode.getPeriode().getFom()))
                .tom(getLocalDateTimeFromLocalDate(ansettelsesperiode.getPeriode().getTom()))
                .build();
    }

    private static RsArbeidsavtale mapRsArbeidsavtale(List<Arbeidsavtale> arbeidsavtaler) {
        if (arbeidsavtaler == null || arbeidsavtaler.isEmpty()) {
            return null;
        }
        var arbeidsavtale = findLatestArbeidsavtale(arbeidsavtaler);
        return RsArbeidsavtale.builder()
                .arbeidstidsordning(arbeidsavtale.getArbeidstidsordning())
                .yrke(arbeidsavtale.getYrke())
                .stillingsprosent(arbeidsavtale.getStillingsprosent())
                .avtaltArbeidstimerPerUke(arbeidsavtale.getAntallTimerPrUke())
                .sisteLoennsendringsdato(getLocalDateTimeFromLocalDate(arbeidsavtale.getSistLoennsendring()))
                .endringsdatoStillingsprosent(getLocalDateTimeFromLocalDate(arbeidsavtale.getSistStillingsendring()))
                .build();
    }

    private static Arbeidsavtale findLatestArbeidsavtale(List<Arbeidsavtale> arbeidsavtaler) {
        var latest = arbeidsavtaler.get(0).getSporingsinformasjon().getEndretTidspunkt();
        var latestArbeidsavtale = arbeidsavtaler.get(0);
        for (var arbeidsavtale : arbeidsavtaler) {
            if (arbeidsavtale.getSporingsinformasjon().getEndretTidspunkt().isAfter(latest)) {
                latest = arbeidsavtale.getSporingsinformasjon().getEndretTidspunkt();
                latestArbeidsavtale = arbeidsavtale;
            }
        }
        return latestArbeidsavtale;
    }

    private static List<RsPermisjon> mapRsPermisjoner(List<PermisjonPermittering> permisjoner) {
        List<RsPermisjon> rsPermisjoner = new ArrayList<>(permisjoner.size());
        for (var permisjon : permisjoner) {
            rsPermisjoner.add(RsPermisjon.builder()
                    .permisjonId(permisjon.getPermisjonPermitteringId())
                    .permisjonsPeriode(RsPeriode.builder()
                            .fom(getLocalDateTimeFromLocalDate(permisjon.getPeriode().getFom()))
                            .tom(getLocalDateTimeFromLocalDate(permisjon.getPeriode().getTom()))
                            .build())
                    .permisjonsprosent(permisjon.getProsent())
                    .permisjonOgPermittering(permisjon.getType())
                    .build());
        }
        return rsPermisjoner;
    }

    private static List<RsAntallTimerForTimeloennet> mapRsAntallTimerForTimeloennet(List<AntallTimerForTimeloennet> antallTimerForTimeloennet) {
        List<RsAntallTimerForTimeloennet> rsAntallTimerForTimeloennet = new ArrayList<>(antallTimerForTimeloennet.size());
        for (var antallTimer : antallTimerForTimeloennet) {
            rsAntallTimerForTimeloennet.add(RsAntallTimerForTimeloennet.builder()
                    .periode(RsPeriode.builder()
                            .fom(getLocalDateTimeFromLocalDate(antallTimer.getPeriode().getFom()))
                            .tom(getLocalDateTimeFromLocalDate(antallTimer.getPeriode().getTom()))
                            .build())
                    .antallTimer(antallTimer.getAntallTimer())
                    .build());
        }
        return rsAntallTimerForTimeloennet;
    }

    private static List<RsUtenlandsopphold> mapRsUtenlandsopphold(List<Utenlandsopphold> utenlandsopphold) {
        List<RsUtenlandsopphold> rsUtenlandsopphold = new ArrayList<>(utenlandsopphold.size());
        for (var opphold : utenlandsopphold) {
            rsUtenlandsopphold.add(RsUtenlandsopphold.builder()
                    .periode(RsPeriode.builder()
                            .fom(getLocalDateTimeFromLocalDate(opphold.getPeriode().getFom()))
                            .tom(getLocalDateTimeFromLocalDate(opphold.getPeriode().getTom()))
                            .build())
                    .land(opphold.getLandkode())
                    .build());
        }
        return rsUtenlandsopphold;
    }

    private static String findStringNullSafe(
            JsonNode node,
            String fieldName
    ) {
        return node.get(fieldName) != null ? node.get(fieldName).asText() : null;
    }

    private static Long findLongNullSafe(
            JsonNode node,
            String fieldName
    ) {
        return node.get(fieldName) != null ? node.get(fieldName).asLong() : null;
    }

    private static Double findDoubleNullSafe(
            JsonNode node,
            String fieldName
    ) {
        return node.get(fieldName) != null ? node.get(fieldName).asDouble() : null;
    }

    private static LocalDate findLocalDateNullSafe(
            JsonNode node,
            String fieldName
    ) {
        return node.get(fieldName) != null ? LocalDate.parse(node.get(fieldName).asText()) : null;
    }

    private static LocalDateTime findLocalDateTimeNullSafe(
            JsonNode node,
            String fieldName
    ) {
        return node.get(fieldName) != null && !"".equals(node.get(fieldName).asText()) ? LocalDateTime.parse(node.get(fieldName).asText()) : null;
    }

    private static YearMonth findYearMonthNullSage(
            JsonNode node,
            String fieldName
    ) {
        return node.get(fieldName) != null ? YearMonth.parse(node.get(fieldName).asText()) : null;
    }
}
