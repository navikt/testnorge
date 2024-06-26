package no.nav.testnav.apps.tenorsearchservice.service;

import no.nav.testnav.apps.tenorsearchservice.domain.TenorOrganisasjonRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOrganisasjonSelectOptions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TenorOrganisasjonEksterneRelasjonerUtilityTest {

    @Test
    void testGetOrganisasjonEksterneRelasjoner() {

        var request = TenorOrganisasjonRequest.builder()
                .tenorRelasjoner(TenorOrganisasjonRequest.TenorRelasjoner.builder()
                        .testinnsendingSkattEnhet(TenorOrganisasjonRequest.TestinnsendingSkattEnhet.builder()
                                .inntektsaar(new BigInteger("2022"))
                                .harSkattemeldingUtkast(true)
                                .harSkattemeldingFastsatt(false)
                                .harSelskapsmeldingUtkast(true)
                                .harSelskapsmeldingFastsatt(false)
                                .build())
                        .arbeidsforhold(TenorOrganisasjonRequest.Arbeidsforhold.builder()
                                .harPermisjoner(true)
                                .harPermitteringer(false)
                                .harTimerMedTimeloenn(true)
                                .harUtenlandsopphold(false)
                                .harHistorikk(true)
                                .arbeidsforholdtype(TenorOrganisasjonSelectOptions.ArbeidsforholdType.OrdinaertArbeidsforhold)
                                .build())
                        .samletReskontroinnsyn(TenorOrganisasjonRequest.SamletReskontroinnsyn.builder()
                                .harKrav(true)
                                .harInnbetaling(false)
                                .build())
                        .tjenestepensjonsavtaleOpplysningspliktig(TenorOrganisasjonRequest.TjenestepensjonsavtaleOpplysningspliktig.builder()
                                .tjenestepensjonsinnretningOrgnr("123456789")
                                .periode("2022")
                                .build())
                        .build())
                .build();

        var result = TenorOrganisasjonEksterneRelasjonerUtility.getOrganisasjonEksterneRelasjoner(request);

        var expected = " and tenorRelasjoner.arbeidsforhold:{harPermisjoner:true and harPermitteringer:false and harTimerMedTimeloenn:true and harUtenlandsopphold:false and harHistorikk:true and arbeidsforholdtype:ordinaertArbeidsforhold} and tenorRelasjoner.testinnsendingSkattEnhet:{inntektsaar:2022 and harSkattemeldingUtkast:* and  not harSkattemeldingFastsatt:* and harSelskapsmeldingUtkast:* and  not harSelskapsmeldingFastsatt:*} and tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig:{tjenestepensjonsinnretningOrgnr:123456789 and periode:2022} and tenorRelasjoner.samletReskontroinnsyn:{harKrav:* and  not harInnbetaling:*}";

        assertEquals(expected, result);
    }

}