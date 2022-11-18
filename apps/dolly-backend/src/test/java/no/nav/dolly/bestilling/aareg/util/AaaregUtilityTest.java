package no.nav.dolly.bestilling.aareg.util;

import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Person;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
class AaaregUtilityTest {

    private static final String ORGNUMMER = "999999999";
    private static final String IDENT = "11111111111";

    private static Arbeidsforhold getArbeidsforhold(String arbeidsforholdId, Boolean isOppdatering) {

        return Arbeidsforhold.builder()
                .arbeidsgiver(Organisasjon.builder()
                        .organisasjonsnummer(ORGNUMMER)
                        .build())
                .arbeidstaker(Person.builder()
                        .offentligIdent(IDENT)
                        .build())
                .arbeidsforholdId(arbeidsforholdId)
                .isOppdatering(isOppdatering)
                .build();
    }

    @ParameterizedTest
    @CsvSource({"1, false, 1, 0, 1",
            "1, true, 1, 1, 0",
            "2, false, 1, 0, 1",
            "2, true, 1, 1, 0"})
    void arbeidsforholdEksistens_eksisterIkke(String reqArbForholdId, Boolean isOppdatering, String respArbForholdId,
                                              int antallEksisterende, int antallNye) {

        var arbeidsforhold = getArbeidsforhold(reqArbForholdId, isOppdatering);
        var response = ArbeidsforholdRespons.builder()
                .eksisterendeArbeidsforhold(List.of(getArbeidsforhold(respArbForholdId, null)))
                .build();

        var ekistens = AaaregUtility.doEksistenssjekk(response, List.of(arbeidsforhold));

        assertThat(ekistens.getEksisterendeArbeidsforhold(), hasSize(antallEksisterende));
        assertThat(ekistens.getNyeArbeidsforhold(), hasSize(antallNye));
    }
}