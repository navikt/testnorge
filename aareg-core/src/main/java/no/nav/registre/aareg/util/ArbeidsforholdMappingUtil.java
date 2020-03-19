package no.nav.registre.aareg.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.aareg.domain.RsAktoer;
import no.nav.registre.aareg.domain.RsAktoerPerson;
import no.nav.registre.aareg.domain.RsArbeidsavtale;
import no.nav.registre.aareg.domain.RsArbeidsforhold;
import no.nav.registre.aareg.domain.RsOrganisasjon;
import no.nav.registre.aareg.domain.RsPeriode;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Ansettelsesperiode;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.AntallTimerForTimeloennet;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Arbeidsavtale;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Arbeidsforhold;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Bruksperiode;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Gyldighetsperiode;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.OpplysningspliktigArbeidsgiver;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Organisasjon;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Periode;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.PermisjonPermittering;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Person;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Sporingsinformasjon;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Utenlandsopphold;

public class ArbeidsforholdMappingUtil {

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
                .sporingsinformasjon(mapSporingsinformasjon(arbeidsforholdNode.get("sporingsinformasjon")))
                .build();
    }

    public static RsArbeidsforhold mapArbeidsforholdToRsArbeidsforhold(Arbeidsforhold arbeidsforhold) {
        return RsArbeidsforhold.builder()
                .arbeidsforholdIDnav(arbeidsforhold.getNavArbeidsforholdId())
                .arbeidsforholdID(arbeidsforhold.getArbeidsforholdId())
                .arbeidstaker(mapRsAktoerPerson(arbeidsforhold.getArbeidstaker()))
                .arbeidsgiver(mapRsAktoer(arbeidsforhold.getArbeidsgiver()))
                .arbeidsforholdstype(arbeidsforhold.getType())
                .ansettelsesPeriode(mapRsAnsettelsesPeriode(arbeidsforhold.getAnsettelsesperiode()))
                .arbeidsavtale(mapRsArbeidsavtale(arbeidsforhold.getArbeidsavtaler()))
                // .permisjon()
                // .antallTimerForTimeloennet()
                // .utenlandsopphold()
                .build();
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
            throw new RuntimeException("Ukjent arbeidsgivertype: " + arbeidsgiverType);
        }
        return opplysningspliktigArbeidsgiver;
    }

    private static Ansettelsesperiode mapAnsettelsesperiode(JsonNode ansettelsesperiode) {
        return Ansettelsesperiode.builder()
                .periode(mapPeriode(ansettelsesperiode.get("periode")))
                .varslingskode(findStringNullSafe(ansettelsesperiode, "varslingskode"))
                .bruksperiode(mapBruksperiode(ansettelsesperiode.get("bruksperiode")))
                .sporingsinformasjon(mapSporingsinformasjon(ansettelsesperiode.get("sporingsinformasjon")))
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
                .sporingsinformasjon(mapSporingsinformasjon(arbeidsavtale.get("sporingsinformasjon")))
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
                    .periode(mapPeriode(permisjonPermittering.get("periode")))
                    .prosent(findDoubleNullSafe(permisjonPermittering, "prosent"))
                    .type(findStringNullSafe(permisjonPermittering, "type"))
                    .varslingskode(findStringNullSafe(permisjonPermittering, "varslingskode"))
                    .sporingsinformasjon(mapSporingsinformasjon(permisjonPermittering.get("sporingsinformasjon")))
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
                    .periode(mapPeriode(antallTimer.get("periode")))
                    .antallTimer(findDoubleNullSafe(antallTimer, "antallTimer"))
                    .rapporteringsperiode(findYearMonthNullSage(antallTimer, "rapporteringsperiode"))
                    .sporingsinformasjon(mapSporingsinformasjon(antallTimer.get("sporingsinformasjon")))
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
                    .periode(mapPeriode(opphold.get("periode")))
                    .landkode(findStringNullSafe(opphold, "landkode"))
                    .rapporteringsperiode(findYearMonthNullSage(opphold, "rapporteringsperiode"))
                    .sporingsinformasjon(mapSporingsinformasjon(opphold.get("sporingsinformasjon")))
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
        return node.get(fieldName) != null ? LocalDateTime.parse(node.get(fieldName).asText()) : null;
    }

    private static YearMonth findYearMonthNullSage(
            JsonNode node,
            String fieldName
    ) {
        return node.get(fieldName) != null ? YearMonth.parse(node.get(fieldName).asText()) : null;
    }

    private static RsAktoerPerson mapRsAktoerPerson(Person person) {
        var rsAktoerPerson = RsAktoerPerson.builder()
                .ident(person.getOffentligIdent())
                .build();
        rsAktoerPerson.setAktoertype("PERS");
        return rsAktoerPerson;
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
            throw new RuntimeException("Ukjent aktørtype: " + opplysningspliktigArbeidsgiver.getType());
        }
    }

    private static RsPeriode mapRsAnsettelsesPeriode(Ansettelsesperiode ansettelsesperiode) {
        return RsPeriode.builder()
                .fom(ansettelsesperiode.getPeriode().getFom() != null ? ansettelsesperiode.getPeriode().getFom().atStartOfDay() : null)
                .tom(ansettelsesperiode.getPeriode().getTom() != null ? ansettelsesperiode.getPeriode().getTom().atStartOfDay() : null)
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
                .sisteLoennsendringsdato(arbeidsavtale.getSistLoennsendring() != null ? arbeidsavtale.getSistLoennsendring().atStartOfDay() : null)
                .endringsdatoStillingsprosent(arbeidsavtale.getSistStillingsendring() != null ? arbeidsavtale.getSistStillingsendring().atStartOfDay() : null)
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
}
