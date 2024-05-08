package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOrganisasjonRequest;

import static java.util.Objects.isNull;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertBooleanWildcard;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertDatoer;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertEnum;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertObject;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.guard;

@UtilityClass
public class TenorOrganisasjonEksterneRelasjonerUtility {

    public static String getOrganisasjonEksterneRelasjoner(TenorOrganisasjonRequest searchData) {

        var tenorRelasjoner = searchData.getTenorRelasjoner();

        if (isNull(tenorRelasjoner)) {
            return "";
        }

        return new StringBuilder()
                .append(getArbeidsforhold(tenorRelasjoner.getArbeidsforhold()))
                .append(getTestinnsendingSkattEnhet(tenorRelasjoner.getTestinnsendingSkattEnhet()))
                .append(getTjenestepensjonsavtaleOpplysningspliktig(tenorRelasjoner.getTjenestepensjonsavtaleOpplysningspliktig()))
                .append(getSamletReskontroinnsyn(tenorRelasjoner.getSamletReskontroinnsyn()))
                .toString();
    }

    private String getSamletReskontroinnsyn(TenorOrganisasjonRequest.SamletReskontroinnsyn reskontroInnsyn) {

        return isNull(reskontroInnsyn) ? "" :
                " and tenorRelasjoner.samletReskontroinnsyn:{%s}".formatted(guard(new StringBuilder()
                        .append(convertBooleanWildcard("harKrav", reskontroInnsyn.getHarKrav()))
                        .append(convertBooleanWildcard("harInnbetaling", reskontroInnsyn.getHarInnbetaling()))));
    }

    private String getTjenestepensjonsavtaleOpplysningspliktig(TenorOrganisasjonRequest.TjenestepensjonsavtaleOpplysningspliktig tpOpplysningspliktig) {

        return isNull(tpOpplysningspliktig) ? "" :
                " and tenorRelasjoner.samletReskontroinnsyn:{%s}".formatted(guard(new StringBuilder()
                        .append(convertObject("tjenestepensjonsinnretningOrgnr", tpOpplysningspliktig.getTjenestepensjonsinnretningOrgnr()))
                        .append(convertObject("periode", tpOpplysningspliktig.getPeriode()))));
    }

    private String getTestinnsendingSkattEnhet(TenorOrganisasjonRequest.TestinnsendingSkattEnhet skattEnhet) {

        return isNull(skattEnhet) ? "" :
                " and tenorRelasjoner.testinnsendingSkattEnhet:{%s}".formatted(guard(new StringBuilder()
                        .append(convertObject("inntektsaar", skattEnhet.getInntektsaar()))
                        .append(convertBooleanWildcard("harSkattemeldingUtkast", skattEnhet.getHarSkattemeldingUtkast()))
                        .append(convertBooleanWildcard("harSkattemeldingFastsatt", skattEnhet.getHarSkattemeldingFastsatt()))
                        .append(convertBooleanWildcard("harSelskapsmeldingUtkast", skattEnhet.getHarSelskapsmeldingUtkast()))
                        .append(convertBooleanWildcard("harSelskapsmeldingFastsatt", skattEnhet.getHarSelskapsmeldingFastsatt()))
                        .append(convertEnum("manglendeGrunnlagsdata", skattEnhet.getManglendeGrunnlagsdata()))
                        .append(convertEnum("manntall", skattEnhet.getManntall()))));
    }

    private String getArbeidsforhold(TenorOrganisasjonRequest.Arbeidsforhold arbeidsforhold) {

        return isNull(arbeidsforhold) ? "" :
                " and tenorRelasjoner.arbeidsforhold:{%s}".formatted(guard(new StringBuilder()
                        .append(convertDatoer("startDato", arbeidsforhold.getStartDato()))
                        .append(convertDatoer("sluttDato", arbeidsforhold.getSluttDato()))
                        .append(convertBooleanWildcard("harPermisjoner", arbeidsforhold.getHarPermisjoner()))
                        .append(convertBooleanWildcard("harPermitteringer", arbeidsforhold.getHarPermitteringer()))
                        .append(convertBooleanWildcard("harTimerMedTimeloenn", arbeidsforhold.getHarTimerMedTimeloenn()))
                        .append(convertBooleanWildcard("harUtenlandsopphold", arbeidsforhold.getHarUtenlandsopphold()))
                        .append(convertBooleanWildcard("harHistorikk", arbeidsforhold.getHarHistorikk()))
                        .append(convertObject("arbeidsforholdtype", arbeidsforhold.getArbeidsforholdtype()))));
    }
}