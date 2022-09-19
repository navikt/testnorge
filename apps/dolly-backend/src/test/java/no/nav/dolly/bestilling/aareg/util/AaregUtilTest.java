package no.nav.dolly.bestilling.aareg.util;

import no.nav.dolly.bestilling.aareg.domain.Aktoer;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class AaregUtilTest {

    private final static String ORGNUMMER = "12345678";
    private final static String IDENT = "12345678901";
    private final static String YRKE = "abc";
    private final static String ARBEIDSTIDSORDNING = "40";
    private final static List<Arbeidsforhold> nyttArbeidsforhold = List.of(
            Arbeidsforhold.builder()
                    .arbeidsavtale(RsArbeidsavtale.builder()
                            .yrke(YRKE)
                            .arbeidstidsordning(ARBEIDSTIDSORDNING)
                            .build())
                    .arbeidsgiver(RsOrganisasjon.builder()
                            .orgnummer(ORGNUMMER)
                            .build())
                    .build());
    ;

    private final static List<ArbeidsforholdResponse> eksisterendeArbeidsforhold = List.of(
            ArbeidsforholdResponse.builder()
                    .arbeidsavtaler(List.of(ArbeidsforholdResponse.Arbeidsavtale.builder()
                            .arbeidstidsordning(ARBEIDSTIDSORDNING)
                            .yrke(YRKE)
                            .build()))
                    .arbeidsgiver(ArbeidsforholdResponse.Arbeidsgiver.builder()
                            .type(Aktoer.ORGANISASJON)
                            .organisasjonsnummer(ORGNUMMER)
                            .build())
                    .build());

    @Test
    public void mergeEksisterendeArbeidsforhold_IgnorerEksisterende() {

        List<Arbeidsforhold> mergedeArbeidsforhold = AaregUtil.merge(nyttArbeidsforhold, eksisterendeArbeidsforhold, IDENT, false);

        assertThat(mergedeArbeidsforhold).isEmpty();
    }

    @Test
    public void mergeIngenEksisterendeArbeidsforhold_OK() {

        List<Arbeidsforhold> mergedeArbeidsforhold = AaregUtil.merge(nyttArbeidsforhold, emptyList(), IDENT, false);

        assertThat(mergedeArbeidsforhold).hasSize(1);
    }

    @Test
    public void mergeIngenNyeArbeidsforhold_OK() {

        List<Arbeidsforhold> mergedeArbeidsforhold = AaregUtil.merge(emptyList(), eksisterendeArbeidsforhold, IDENT, false);

        assertThat(mergedeArbeidsforhold).isEmpty();
    }
}