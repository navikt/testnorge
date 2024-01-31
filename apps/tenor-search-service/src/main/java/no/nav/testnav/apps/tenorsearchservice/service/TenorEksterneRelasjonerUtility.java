package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertBooleanSpecial;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertBooleanWildcard;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertDatoer;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertEnum;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertIntervall;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertObject;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertPeriode;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.guard;

@UtilityClass
public class TenorEksterneRelasjonerUtility {

    private static final String INNTEKTSAAR = "inntektsaar";
    private static final String TYPE_OPPGJOER = "typeOppgjoer";
    private static final String AND = " and ";


    public static String getEksterneRelasjoner(TenorRequest searchData) {

        return new StringBuilder()
                .append(getRoller(searchData.getRoller()))
                .append(getTjenestepensjonsavtale(searchData.getTjenestepensjonsavtale()))
                .append(getSkattemelding(searchData.getSkattemelding()))
                .append(getInntektAordningen(searchData.getInntekt()))
                .append(getSkatteplikt(searchData.getSkatteplikt()))
                .append(getTilleggsskatt(searchData.getTilleggsskatt()))
                .append(getArbeidsforhold(searchData.getArbeidsforhold()))
                .append(getBeregnetSkatt(searchData.getBeregnetSkatt()))
                .append(getTestinnsendingSkattPerson(searchData.getTestinnsendingSkattPerson()))
                .append(getSamletReskontroInnsyn(searchData.getSamletReskontroInnsyn()))
                .append(getSummertSkattegrunnlag(searchData.getSummertSkattegrunnlag()))
                .append(getSpesifisertSummertSkattegrunnlag(searchData.getSpesifisertSummertSkattegrunnlag()))
                .toString();
    }

    private String getSpesifisertSummertSkattegrunnlag(TenorRequest.SpesisfisertSummertSkattegrunnlag spesisfisertSummertSkattegrunnlag) {

        return isNull(spesisfisertSummertSkattegrunnlag) ? "" :
                " and tenorRelasjoner.spesifisertSummertSkattegrunnlag:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertObject(INNTEKTSAAR, spesisfisertSummertSkattegrunnlag.getInntektsaar()))
                                .append(convertEnum("stadie", spesisfisertSummertSkattegrunnlag.getStadietype()))
                                .append(convertEnum(TYPE_OPPGJOER, spesisfisertSummertSkattegrunnlag.getOppgjoerstype()))
                                .append(convertEnum("tekniskNavn", spesisfisertSummertSkattegrunnlag.getTekniskNavn()))
                                .append(convertEnum("spesifiseringstype", spesisfisertSummertSkattegrunnlag.getSpesifiseringstype()))
                                .append(convertIntervall("alminneligInntektFoerSaerfradragBeloep",
                                        spesisfisertSummertSkattegrunnlag.getAlminneligInntektFoerSaerfradragBeloep()))
                        ));
    }

    private String getSummertSkattegrunnlag(TenorRequest.SummertSkattegrunnlag summertSkattegrunnlag) {

        return isNull(summertSkattegrunnlag) ? "" :
                " and tenorRelasjoner.summertSkattegrunnlag:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertObject(INNTEKTSAAR, summertSkattegrunnlag.getInntektsaar()))
                                .append(convertEnum("stadie", summertSkattegrunnlag.getStadietype()))
                                .append(convertEnum(TYPE_OPPGJOER, summertSkattegrunnlag.getOppgjoerstype()))
                                .append(convertEnum("tekniskNavn", summertSkattegrunnlag.getTekniskNavn()))
                                .append(convertIntervall("alminneligInntektFoerSaerfradragBeloep",
                                        summertSkattegrunnlag.getAlminneligInntektFoerSaerfradragBeloep()))
                        ));
    }

    private String getSamletReskontroInnsyn(TenorRequest.SamletReskontroInnsyn samletReskontroInnsyn) {

        return isNull(samletReskontroInnsyn) ? "" :
                " and tenorRelasjoner.samletReskontroinnsyn:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertBooleanWildcard("harKrav", samletReskontroInnsyn.getHarKrav()))
                                .append(convertBooleanWildcard("harInnbetaling", samletReskontroInnsyn.getHarInnbetaling()))
                        ));
    }

    private String getTestinnsendingSkattPerson(TenorRequest.TestinnsendingSkattPerson testinnsendingSkattPerson) {

        return isNull(testinnsendingSkattPerson) ? "" :
                " and tenorRelasjoner.testinnsendingSkattPerson:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertObject(INNTEKTSAAR, testinnsendingSkattPerson.getInntektsaar()))
                                .append(convertBooleanSpecial("harSkattemeldingUtkast", testinnsendingSkattPerson.getHarSkattemeldingUtkast()))
                                .append(convertBooleanSpecial("harSkattemeldingFastsatt", testinnsendingSkattPerson.getHarSkattemeldingFastsatt()))
                        ));
    }

    private String getBeregnetSkatt(TenorRequest.BeregnetSkatt beregnetSkatt) {

        return isNull(beregnetSkatt) ? "" :
                " and tenorRelasjoner.beregnetSkatt:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertObject(INNTEKTSAAR, beregnetSkatt.getInntektsaar()))
                                .append(convertEnum(TYPE_OPPGJOER, beregnetSkatt.getOppgjoerstype()))
                                .append(convertObject("pensjonsgivendeInntekt", beregnetSkatt.getPensjonsgivendeInntekt()))
                        ));
    }

    private String getArbeidsforhold(TenorRequest.Arbeidsforhold arbeidsforhold) {

        return isNull(arbeidsforhold) ? "" :
                " and tenorRelasjoner.arbeidsforhold:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertDatoer("startDato", arbeidsforhold.getStartDatoPeriode()))
                                .append(convertDatoer("sluttDato", arbeidsforhold.getSluttDatoPeriode()))
                                .append(convertObject("harPermisjoner", arbeidsforhold.getHarPermisjoner()))
                                .append(convertObject("harPermitteringer", arbeidsforhold.getHarPermitteringer()))
                                .append(convertObject("harArbeidsgiver", arbeidsforhold.getHarArbeidsgiver()))
                                .append(convertObject("harTimerMedTimeloenn", arbeidsforhold.getHarTimerMedTimeloenn()))
                                .append(convertObject("harUtenlandsopphold", arbeidsforhold.getHarUtenlandsopphold()))
                                .append(convertObject("harHistorikk", arbeidsforhold.getHarHistorikk()))
                                .append(convertEnum("arbeidsforholdtype", arbeidsforhold.getArbeidsforholdstype()))
                        ));
    }

    private String getTilleggsskatt(TenorRequest.Tilleggsskatt tilleggsskatt) {

        return isNull(tilleggsskatt) ? "" :
                " and tenorRelasjoner.tilleggsskatt:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertObject(INNTEKTSAAR, tilleggsskatt.getInntektsaar()))
                                .append(getTilleggsskattTyper(tilleggsskatt.getTilleggsskattTyper()))

                        ));
    }

    private String getInntektAordningen(TenorRequest.Inntekt inntekt) {

        return (isNull(inntekt)) ? "" :
                " and tenorRelasjoner.inntekt:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertPeriode(inntekt.getPeriode()))
                                .append(convertObject("opplysningspliktig", inntekt.getOpplysningspliktig()))
                                .append(getInntektstyper(inntekt.getInntektstyper()))
                                .append(convertEnum("beskrivelse", inntekt.getBeskrivelse()))
                                .append(getForskuddstrekk(inntekt.getForskuddstrekk()))
                                .append(convertObject("harHistorikk", inntekt.getHarHistorikk()))
                        ));
    }

    private String getForskuddstrekk(List<TenorRequest.Forskuddstrekk> forskuddstrekk1) {

        return forskuddstrekk1.isEmpty() ? "" : " and forskuddstrekk:(%s)"
                .formatted(forskuddstrekk1.stream()
                        .map(Enum::name)
                        .map(type -> "%s%s".formatted(type.substring(0, 1).toLowerCase(), type.substring(1)))
                        .collect(Collectors.joining(AND)));
    }

    private String getInntektstyper(List<TenorRequest.Inntektstype> inntektstyper) {

        return inntektstyper.isEmpty() ? "" : " and inntektstype:(%s)"
                .formatted(inntektstyper.stream()
                        .map(Enum::name)
                        .map(type -> "%s%s".formatted(type.substring(0, 1).toLowerCase(), type.substring(1)))
                        .collect(Collectors.joining(AND)));
    }

    private String getSkattemelding(TenorRequest.Skattemelding skattemelding) {

        return isNull(skattemelding) ? "" :
                " and tenorRelasjoner.skattemelding:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertObject(INNTEKTSAAR, skattemelding.getInntektsaar()))
                                .append(convertEnum("skattemeldingstype", skattemelding.getSkattemeldingstype()))
                        ));
    }

    private String getTjenestepensjonsavtale(TenorRequest.Tjenestepensjonsavtale tjenestepensjonsavtale) {

        return isNull(tjenestepensjonsavtale) ? "" :
                " and tenorRelasjoner.tjenestepensjonavtale:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertObject("pensjonsinnretningOrgnr", tjenestepensjonsavtale.getPensjonsinnretningOrgnr()))
                                .append(convertObject("periode", tjenestepensjonsavtale.getPeriode()))
                        ));
    }

    private String getRoller(List<TenorRequest.Rolle> roller) {

        return (roller.isEmpty()) ? "" : " and tenorRelasjoner.brreg-er-fr:{%s}".formatted(roller.stream()
                .map(Enum::name)
                .map(type -> "%s%s:*".formatted(type.substring(0, 1).toLowerCase(), type.substring(1)))
                .collect(Collectors.joining(AND)));
    }

    private String getSkatteplikt(TenorRequest.Skatteplikt skatteplikt) {

        return isNull(skatteplikt) ? "" :
                " and tenorRelasjoner.skatteplikt:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertObject(INNTEKTSAAR, skatteplikt.getInntektsaar()))
                                .append(getSkattepliktstyper(skatteplikt.getSkattepliktstyper()))
                                .append(convertEnum("saerskiltSkatteplikt", skatteplikt.getSaerskiltSkatteplikt()))
                        ));
    }

    private String getTilleggsskattTyper(List<TenorRequest.TilleggsskattType> tilleggsskattTyper) {

        return tilleggsskattTyper.isEmpty() ? "" : AND +
                tilleggsskattTyper.stream()
                        .map(Enum::name)
                        .map(type -> "%s%s".formatted(type.substring(0, 1).toLowerCase(), type.substring(1)))
                        .map("%s:*"::formatted)
                        .collect(Collectors.joining(AND));
    }

    private String getSkattepliktstyper(List<TenorRequest.Skattepliktstype> skattepliktstyper) {

        return skattepliktstyper.isEmpty() ? "" : AND +
                skattepliktstyper.stream()
                        .map(Enum::name)
                        .map(type -> "%s%s".formatted(type.substring(0, 1).toLowerCase(), type.substring(1)))
                        .map("%s:*"::formatted)
                        .collect(Collectors.joining(AND));
    }
}