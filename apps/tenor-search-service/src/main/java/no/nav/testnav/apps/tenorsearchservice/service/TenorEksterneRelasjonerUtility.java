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
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertEnumWildcard;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertIntervall;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertObject;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertPeriode;

@UtilityClass
public class TenorEksterneRelasjonerUtility {
    
    private static final String INNTEKTSAAR = "inntektsaar";
    private static final String AND = " and ";


    public static String getEksterneRelasjoner(TenorRequest searchData) {

        var builder = new StringBuilder()
                .append(getRoller(searchData.getRoller()))
                .append(getTjenestepensjonsavtale(searchData.getTjenestepensjonsavtale()))
                .append(getSkattemelding(searchData.getSkattemelding()))
                .append(getInntektAordningen(searchData.getInntektAordningen()))
                .append(getSkatteplikt(searchData.getSkatteplikt()))
                .append(getTilleggsskatt(searchData.getTilleggsskatt()))
                .append(getArbeidsforhold(searchData.getArbeidsforhold()))
                .append(getBeregnetSkatt(searchData.getBeregnetSkatt()))
                .append(getOpplysningerFraSkatteetatensInnsendingsMiljoe(searchData.getOpplysningerFraSkatteetatensInnsendingsmiljoe()))
                .append(getSamletReskontroInnsyn(searchData.getSamletReskontroInnsyn()))
                .append(getSummertSkattegrunnlag(searchData.getSummertSkattegrunnlag()))
                .append(getSpesifisertSummertSkattegrunnlag(searchData.getSpesisfisertSummertSkattegrunnlag()));

        return builder.toString();
    }

    private String getSpesifisertSummertSkattegrunnlag(TenorRequest.SpesisfisertSummertSkattegrunnlag spesisfisertSummertSkattegrunnlag) {

        return isNull(spesisfisertSummertSkattegrunnlag) ? "" :
                " and tenorRelasjoner.spesifisertSummertSkattegrunnlag:{%s}".formatted(new StringBuilder()
                        .append(convertObject(INNTEKTSAAR, spesisfisertSummertSkattegrunnlag.getInntektsaar()))
                        .append(convertEnumWildcard(spesisfisertSummertSkattegrunnlag.getStadietype()))
                        .append(convertEnumWildcard(spesisfisertSummertSkattegrunnlag.getOppgjoerstype()))
                        .append(convertEnum("tekniskNavn", spesisfisertSummertSkattegrunnlag.getTekniskNavn()))
                        .append(convertEnum("spesifiseringstype", spesisfisertSummertSkattegrunnlag.getSpesifiseringstype()))
                        .append(convertIntervall("alminneligInntektFoerSaerfradragBeloep",
                                spesisfisertSummertSkattegrunnlag.getAlminneligInntektFoerSaerfradragBeloep()))
                        .substring(5));
    }

    private String getSummertSkattegrunnlag(TenorRequest.SummertSkattegrunnlag summertSkattegrunnlag) {

        return isNull(summertSkattegrunnlag) ? "" :
                " and tenorRelasjoner.summertSkattegrunnlag:{%s}".formatted(new StringBuilder()
                        .append(convertObject(INNTEKTSAAR, summertSkattegrunnlag.getInntektsaar()))
                        .append(convertEnumWildcard(summertSkattegrunnlag.getStadietype()))
                        .append(convertEnum("typeOppgjoer", summertSkattegrunnlag.getOppgjoerstype()))
                        .append(convertEnumWildcard(summertSkattegrunnlag.getTekniskNavn()))
                        .append(convertIntervall("alminneligInntektFoerSaerfradragBeloep",
                                summertSkattegrunnlag.getAlminneligInntektFoerSaerfradragBeloep()))
                        .substring(5));
    }

    private String getSamletReskontroInnsyn(TenorRequest.SamletReskontroInnsyn samletReskontroInnsyn) {

        return isNull(samletReskontroInnsyn) ? "" :
                " and tenorRelasjoner.samletReskontroinnsyn{%s}".formatted(new StringBuilder()
                        .append(convertBooleanWildcard("harKrav", samletReskontroInnsyn.getHarKrav()))
                        .append(convertBooleanWildcard("harInnbetaling", samletReskontroInnsyn.getHarInnbetaling()))
                        .substring(5));
    }

    private String getOpplysningerFraSkatteetatensInnsendingsMiljoe(TenorRequest.OpplysningerFraSkatteetatensInnsendingsmiljoe opplysningerFraSkatteetatensInnsendingsmiljoe) {

        return isNull(opplysningerFraSkatteetatensInnsendingsmiljoe) ? "" :
                " and tenorRelasjoner.testinnsendingSkattPerson:{%s}".formatted(new StringBuilder()
                        .append(convertObject(INNTEKTSAAR, opplysningerFraSkatteetatensInnsendingsmiljoe.getInntektsaar()))
                        .append(convertBooleanSpecial("harSkattemeldingUtkast", opplysningerFraSkatteetatensInnsendingsmiljoe.getHarSkattemeldingUtkast()))
                        .append(convertBooleanSpecial("harSkattemeldingFastsatt", opplysningerFraSkatteetatensInnsendingsmiljoe.getHarSkattemeldingFastsatt()))
                        .substring(5));
    }

    private String getBeregnetSkatt(TenorRequest.BeregnetSkatt beregnetSkatt) {

        return isNull(beregnetSkatt) ? "" :
                " and tenorRelasjoner.beregnetSkatt:{%s}".formatted(new StringBuilder()
                        .append(convertObject(INNTEKTSAAR, beregnetSkatt.getInntektsaar()))
                        .append(convertEnum("typeOppgjoer", beregnetSkatt.getOppgjoerstype()))
                        .append(convertObject("pensjonsgivendeInntekt", beregnetSkatt.getPensjonsgivendeInntekt()))
                        .substring(5));
    }

    private String getArbeidsforhold(TenorRequest.Arbeidsforhold arbeidsforhold) {

        return isNull(arbeidsforhold) ? "" :
                " and tenorRelasjoner.arbeidsforhold:{%s}".formatted(new StringBuilder()
                        .append(convertDatoer("startDato", arbeidsforhold.getStartDatoPeriode()))
                        .append(convertDatoer("sluttDato", arbeidsforhold.getSluttDatoPeriode()))
                        .append(convertObject("harPermisjoner", arbeidsforhold.getHarPermisjoner()))
                        .append(convertObject("harPermitteringer", arbeidsforhold.getHarPermitteringer()))
                        .append(convertObject("harArbeidsgiver", arbeidsforhold.getHarArbeidsgiver()))
                        .append(convertObject("harTimerMedTimeloenn", arbeidsforhold.getHarTimerMedTimeloenn()))
                        .append(convertObject("harUtenlandsopphold", arbeidsforhold.getHarUtenlandsopphold()))
                        .append(convertObject("harHistorikk", arbeidsforhold.getHarHistorikk()))
                        .append(convertEnum("arbeidsforholdtype", arbeidsforhold.getArbeidsforholdstype()))
                        .substring(5));
    }

    private String getTilleggsskatt(TenorRequest.Tilleggsskatt tilleggsskatt) {

        return isNull(tilleggsskatt) ? "" :
                " and tenorRelasjoner.tilleggsskatt:{%s}".formatted(
                        new StringBuilder()
                                .append(convertObject(INNTEKTSAAR, tilleggsskatt.getInntektsaar()))
                                .append(getTilleggsskattTyper(tilleggsskatt.getTilleggsskattTyper()))
                                .substring(5));
    }

    private String getInntektAordningen(TenorRequest.InntektAordningen inntektAordningen) {

        return (isNull(inntektAordningen)) ? "" :
                " and tenorRelasjoner.inntekt:{%s}".formatted(new StringBuilder()
                        .append(convertPeriode(inntektAordningen.getPeriode()))
                        .append(convertObject("opplysningspliktig", inntektAordningen.getOpplysningspliktig()))
                        .append(getInntektstyper(inntektAordningen.getInntektstyper()))
                        .append(convertEnum("beskrivelse", inntektAordningen.getBeskrivelse()))
                        .append(getForskuddstrekk(inntektAordningen.getForskuddstrekk()))
                        .append(convertObject("harHistorikk", inntektAordningen.getHarHistorikk()))
                        .substring(5));
    }

    private String getForskuddstrekk(List<TenorRequest.Forskuddstrekk> forskuddstrekk1) {

        return forskuddstrekk1.isEmpty() ? "" : " and forskuddstrekk:(%s)"
                .formatted(forskuddstrekk1.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(AND)));
    }

    private String getInntektstyper(List<TenorRequest.Inntektstype> inntektstyper) {

        return inntektstyper.isEmpty() ? "" : " and inntektstype:(%s)"
                .formatted(inntektstyper.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(AND)));
    }

    private String getSkattemelding(TenorRequest.Skattemelding skattemelding) {

        return isNull(skattemelding) ? "" :
                " and tenorRelasjoner.skattemelding:{%s}".formatted(new StringBuilder()
                        .append(convertObject(INNTEKTSAAR, skattemelding.getInntektsaar()))
                        .append(convertEnum("skattemeldingstype", skattemelding.getSkattemeldingstype()))
                        .substring(5));
    }

    private String getTjenestepensjonsavtale(TenorRequest.Tjenestepensjonsavtale tjenestepensjonsavtale) {

        return isNull(tjenestepensjonsavtale) ? "" :
                " and tenorRelasjoner.tjenestepensjonavtale:{%s}".formatted(new StringBuilder()
                        .append(convertObject("pensjonsinnretningOrgnr", tjenestepensjonsavtale.getPensjonsinnretningOrgnr()))
                        .append(convertObject("periode", tjenestepensjonsavtale.getPeriode()))
                        .substring(5));
    }

    private String getRoller(List<TenorRequest.Roller> roller) {

        return (roller.isEmpty()) ? "" : " and tenorRelasjoner.brreg-er-fr:{dagligLeder:*}";
    }

    private String getSkatteplikt(TenorRequest.Skatteplikt skatteplikt) {

        return isNull(skatteplikt) ? "" :
                " and tenorRelasjoner.skatteplikt:{%s}".formatted(
                        new StringBuilder()
                                .append(convertObject(INNTEKTSAAR, skatteplikt.getInntektsaar()))
                                .append(getSkattepliktstyper(skatteplikt.getSkattepliktstyper()))
                                .append(convertEnumWildcard(skatteplikt.getSaerskiltSkatteplikt()))
                                .substring(5));
    }

    private String getTilleggsskattTyper(List<TenorRequest.TilleggsskattType> tilleggsskattTyper) {

        return tilleggsskattTyper.isEmpty() ? "" : AND +
                tilleggsskattTyper.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(AND));
    }

    private String getSkattepliktstyper(List<TenorRequest.Skattepliktstype> skattepliktstyper) {

        return skattepliktstyper.isEmpty() ? "" : AND +
                skattepliktstyper.stream()
                        .map(Enum::name)
                        .map("%s:*"::formatted)
                        .collect(Collectors.joining(AND));
    }
}