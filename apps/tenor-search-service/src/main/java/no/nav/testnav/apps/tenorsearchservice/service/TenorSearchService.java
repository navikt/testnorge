package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.TenorClient;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenorSearchService {

    private final TenorClient tenorClient;

    public Mono<TenorResponse> getTestdata(String testDataQuery) {

        return tenorClient.getTestdata(isNotBlank(testDataQuery) ? testDataQuery : "");
    }

    public Mono<TenorResponse> getTestdata(TenorRequest searchData) {

        var builder = new StringBuilder()
                .append(convertObject("identifikator", searchData.getIdentifikator()))
                .append(convertDatoer("foedselsdato", searchData.getFoedselsdato()))
                .append(convertDatoer("doedsdato", searchData.getDoedsdato()))
                .append(getIdentifikatorType(searchData.getIdentifikatorType()))
                .append(convertEnum("kjoenn", searchData.getKjoenn()))
                .append(convertEnum("personstatus", searchData.getPersonstatus()))
                .append(convertEnum("sivilstatus", searchData.getSivilstatus()))
                .append(getUtenlandskPersonidentifikasjon(searchData.getUtenlandskPersonIdentifikasjon()))
                .append(convertEnum("identitetsgrunnlagStatus", searchData.getIdentitetsgrunnlagStatus()))
                .append(convertEnum("adressebeskyttelse", searchData.getAdressebeskyttelse()))
                .append(convertObject("legitimasjonsdokument", searchData.getHarLegitimasjonsdokument()))
                .append(convertObject("falskIdentitet", searchData.getHarFalskIdentitet()))
                .append(convertObject("norskStatsborgerskap", searchData.getHarNorskStatsborgerskap()))
                .append(convertObject("flereStatsborgerskap", searchData.getHarFlereStatsborgerskap()));

        if (nonNull(searchData.getNavn())) {
            builder.append(convertObject("flereFornavn", searchData.getNavn().getHarFlereFornavn()))
                    .append(getIntervall("navnLengde", searchData.getNavn().getNavnLengde()))
                    .append(convertBooleanWildcard("mellomnavn", searchData.getNavn().getHarMellomnavn()))
                    .append(convertObject("navnSpesialtegn", searchData.getNavn().getHarNavnSpesialtegn()));
        }

        if (nonNull(searchData.getAdresser())) {
            builder.append(convertObject("bostedsadresse", searchData.getAdresser().getBostedsadresseFritekst()))
                    .append(convertBooleanWildcard("bostedsadresse", searchData.getAdresser().getHarBostedsadresse()))
                    .append(convertBooleanWildcard("oppholdAnnetSted", searchData.getAdresser().getHarOppholdAnnetSted()))
                    .append(convertBooleanWildcard("postadresse", searchData.getAdresser().getHarPostadresseNorge()))
                    .append(convertBooleanWildcard("postadresseUtland", searchData.getAdresser().getHarPostadresseUtland()))
                    .append(convertBooleanWildcard("kontaktinfoDoedsbo", searchData.getAdresser().getHarKontaktadresseDoedsbo()))
                    .append(convertObject("adresseSpesialtegn", searchData.getAdresser().getHarAdresseSpesialtegn()))
                    .append(convertEnum("bostedsadresseType", searchData.getAdresser().getBostedsadresseType()))
                    .append(convertEnum("coAdressenavnType", searchData.getAdresser().getCoAdresseType()));
        }

        if (nonNull(searchData.getRelasjoner())) {
            builder.append(getRelasjon(searchData.getRelasjoner().getRelasjon()))
                    .append(getIntervall("antallBarn", searchData.getRelasjoner().getAntallBarn()))
                    .append(convertBooleanWildcard("foreldreansvar", searchData.getRelasjoner().getHarForeldreAnsvar()))
                    .append(getRelasjonMedFoedselsdato(searchData.getRelasjoner().getRelasjonMedFoedselsaar()))
                    .append(convertBooleanWildcard("deltBosted", searchData.getRelasjoner().getHarDeltBosted()))
                    .append(convertBooleanWildcard("vergemaalType", searchData.getRelasjoner().getHarVergemaalEllerFremtidsfullmakt()))
                    .append(convertObject("borMedFar", searchData.getRelasjoner().getBorMedFar()))
                    .append(convertObject("borMedMor", searchData.getRelasjoner().getBorMedMor()))
                    .append(convertObject("borMedMedMor", searchData.getRelasjoner().getBorMedMedmor()));
        }

        if (nonNull(searchData.getHendelser())) {
            builder.append(convertEnum("hendelserMedSekvens.hendelse", searchData.getHendelser().getHendelse()))
                    .append(convertEnum("sisteHendelse", searchData.getHendelser().getSisteHendelse()));
        }
        builder.append(getRoller(searchData.getRoller()))
                .append(getTjenestepensjonsavtale(searchData.getTjenestepensjonsavtale()))
                .append(getSkattemelding(searchData.getSkattemelding()))
                .append(getInntektAordningen(searchData.getInntektAordningen()))
                .append(getSkatteplikt(searchData.getSkatteplikt()))
                .append(getTilleggsskatt(searchData.getTilleggsskatt()))
                .append(getArbeidsforhold(searchData.getArbeidsforhold()))
                .append(getBeregnetSkatt(searchData.getBeregnetSkatt()))
                .append(getOpplysningerFraSkatteetatensInnsendingsMiljoe(searchData.getOpplysningerFraSkatteetatensInnsendingsmiljoe()));

        log.info("Søker med query: {}", builder.substring(5));
        return tenorClient.getTestdata(!builder.isEmpty() ? builder.substring(5) : "");
    }

    private String getOpplysningerFraSkatteetatensInnsendingsMiljoe(TenorRequest.OpplysningerFraSkatteetatensInnsendingsmiljoe opplysningerFraSkatteetatensInnsendingsmiljoe) {

        return isNull(opplysningerFraSkatteetatensInnsendingsmiljoe) ? "" :
                " and tenorRelasjoner.testinnsendingSkattPerson:{%s}".formatted(new StringBuilder()
                        .append(convertObject("inntektsaar", opplysningerFraSkatteetatensInnsendingsmiljoe.getInntektsaar()))
                        .append(convertBooleanSpecial("harSkattemeldingUtkast", opplysningerFraSkatteetatensInnsendingsmiljoe.getHarSkattemeldingUtkast()))
                        .append(convertBooleanSpecial("harSkattemeldingFastsatt", opplysningerFraSkatteetatensInnsendingsmiljoe.getHarSkattemeldingFastsatt()))
                        .substring(5));
    }

    private String convertBooleanSpecial(String navn, Boolean verdi) {

        return isNull(verdi) ? "" : " and %s%s:*".formatted((isFalse(verdi) ? " not " : ""), navn);
    }

    private String getBeregnetSkatt(TenorRequest.BeregnetSkatt beregnetSkatt) {

        return isNull(beregnetSkatt) ? "" :
                " and tenorRelasjoner.beregnetSkatt:{%s}".formatted(new StringBuilder()
                        .append(convertObject("inntektsaar", beregnetSkatt.getInntektsaar()))
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
                        .append(convertObject("harTimerMedLoenn", arbeidsforhold.getHarTimerMedTimeloenn()))
                        .append(convertObject("harUtenlandsopphold", arbeidsforhold.getHarUtenlandsopphold()))
                        .append(convertObject("harHistorikk", arbeidsforhold.getHarHistorikk()))
                        .append(convertEnum("arbeidsforholdtype", arbeidsforhold.getArbeidsforholdstype()))
                        .substring(5));
    }

    private String getTilleggsskatt(TenorRequest.Tilleggsskatt tilleggsskatt) {

        return isNull(tilleggsskatt) ? "" :
                " and tenorRelasjoner.tilleggsskatt:{%s}".formatted(
                        new StringBuilder()
                                .append(convertObject("inntektsaar", tilleggsskatt.getInntektsaar()))
                                .append(getTilleggsskattTyper(tilleggsskatt.getTilleggsskattTyper()))
                                .substring(5));
    }

    private String getTilleggsskattTyper(List<TenorRequest.TilleggsskattType> tilleggsskattTyper) {

        return tilleggsskattTyper.isEmpty() ? "" : " and " +
                tilleggsskattTyper.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(" and "));
    }

    private String getSkatteplikt(TenorRequest.Skatteplikt skatteplikt) {

        return isNull(skatteplikt) ? "" :
                " and tenorRelasjoner.skatteplikt:{%s}".formatted(
                        new StringBuilder()
                                .append(convertObject("inntektsaar", skatteplikt.getInntektsaar()))
                                .append(getSkattepliktstyper(skatteplikt.getSkattepliktstyper()))
                                .append(convertEnumWildcard(skatteplikt.getSaerskiltSkatteplikt()))
                                .substring(5));
    }

    private String getSkattepliktstyper(List<TenorRequest.Skattepliktstype> skattepliktstyper) {

        return skattepliktstyper.isEmpty() ? "" : " and " +
                skattepliktstyper.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(" and "));
    }

    private String convertEnumWildcard(Enum<?> type) {

        return isNull(type) ? "" : " and %s%s:*".formatted(type.name().substring(0, 1).toLowerCase(),
                type.name().substring(1));
    }

    private String getInntektAordningen(TenorRequest.InntektAordningen inntektAordningen) {

        return (isNull(inntektAordningen)) ? "" :
                " and tenorRelasjoner.inntekt:{%s}".formatted(new StringBuilder()
                        .append(getPeriode(inntektAordningen.getPeriode()))
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
                        .collect(Collectors.joining(" and ")));
    }

    private String getInntektstyper(List<TenorRequest.Inntektstype> inntektstyper) {

        return inntektstyper.isEmpty() ? "" : " and inntektstype:(%s)"
                .formatted(inntektstyper.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(" and ")));
    }

    private String getSkattemelding(TenorRequest.Skattemelding skattemelding) {

        return isNull(skattemelding) ? "" :
                " and tenorRelasjoner.skattemelding:{%s}".formatted(new StringBuilder()
                .append(convertObject("inntektsaar", skattemelding.getInntektsaar()))
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

    private String convertObject(String navn, Object verdi) {

        return isNull(verdi) || verdi instanceof String string && isBlank(string) ? "" : " and %s:%s".formatted(navn, verdi);
    }

    private String getRelasjonMedFoedselsdato(TenorRequest.Intervall relasjonMedFoedselsaar) {

        return isNull(relasjonMedFoedselsaar) ? "" : " and tenorRelasjoner.freg:{foedselsdato:[%sto%s]}"
                .formatted(
                        isNull(relasjonMedFoedselsaar.getFraOgMed()) ? "*" : relasjonMedFoedselsaar.getFraOgMed(),
                        isNull(relasjonMedFoedselsaar.getTilOgMed()) ? "*" : relasjonMedFoedselsaar.getTilOgMed());
    }

    private String getRelasjon(TenorRequest.Relasjon relasjon) {

        return isNull(relasjon) ? "" : " and tenorRelasjoner.freg:{tenorRelasjonsnavn:%s}".formatted(relasjon.name());
    }

    private String convertBooleanWildcard(String booleanNavn, Boolean booleanVerdi) {

        return isNotTrue(booleanVerdi) ? "" : " and %s:*".formatted(booleanNavn);
    }

    private String getIntervall(String intervallNavn, TenorRequest.Intervall intervall) {

        return isNull(intervall) ? "" : " and %s:[%sto%s]"
                .formatted(intervallNavn,
                        isNull(intervall.getFraOgMed()) ? "*" : intervall.getFraOgMed(),
                        isNull(intervall.getTilOgMed()) ? "*" : intervall.getTilOgMed());
    }

    private String getPeriode(TenorRequest.MonthInterval intervall) {

        return isNull(intervall) ? "" : " and periode:[%sto%s]"
                .formatted(
                        isNull(intervall.getFraOgMed()) ? "*" : intervall.getFraOgMed(),
                        isNull(intervall.getTilOgMed()) ? "*" : intervall.getTilOgMed());
    }

    private String getUtenlandskPersonidentifikasjon(List<TenorRequest.UtenlandskPersonIdentifikasjon> utenlandskPersonIdentifikasjon) {

        return (utenlandskPersonIdentifikasjon.isEmpty()) ? "" : " and utenlandskPersonidentifikasjon:(%s)"
                .formatted(utenlandskPersonIdentifikasjon.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(" and ")));
    }

    private String getRoller(List<TenorRequest.Roller> roller) {

        return (roller.isEmpty()) ? "" : " and tenorRelasjoner.brreg-er-fr:{dagligLeder:*}";
    }

    private String convertEnum(String enumNavn, Enum<?> enumVerdi) {

        return isNull(enumVerdi) ? "" : " and %s:%s".formatted(enumNavn, enumVerdi);
    }

    private String convertDatoer(String datoNavn, TenorRequest.DatoIntervall datoIntervall) {

        return isNull(datoIntervall) ? "" :
                " and %s:[%sto%s]".formatted(datoNavn, datoIntervall.getFraOgMed(), datoIntervall.getTilOgMed());
    }

    private String getIdentifikatorType(TenorRequest.IdentifikatorType identifikatorType) {

        return isNull(identifikatorType) ? "" :
                " and identifikatorType:%s".formatted(switch (identifikatorType) {
                    case FNR -> "foedselsnummer";
                    case DNR -> "dNummer";
                    case FNR_TIDLIGERE_DNR -> "foedselsnummerOgTidligereDNummer";
                });
    }
}
