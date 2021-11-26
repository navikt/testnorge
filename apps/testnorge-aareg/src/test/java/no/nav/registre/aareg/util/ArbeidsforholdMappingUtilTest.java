package no.nav.registre.aareg.util;

import static no.nav.registre.aareg.testutils.ResourceUtils.getResourceFileContent;
import static no.nav.registre.aareg.util.ArbeidsforholdMappingUtil.mapArbeidsforholdToRsArbeidsforhold;
import static no.nav.registre.aareg.util.ArbeidsforholdMappingUtil.mapToArbeidsforhold;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import no.nav.registre.aareg.domain.RsOrganisasjon;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Ansettelsesperiode;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.AntallTimerForTimeloennet;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Arbeidsavtale;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Organisasjon;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Periode;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.PermisjonPermittering;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Person;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Sporingsinformasjon;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Utenlandsopphold;

@ExtendWith(MockitoExtension.class)
public class ArbeidsforholdMappingUtilTest {

    @Test
    public void shouldMapJsonNodeToArbeidsforhold() throws IOException {
        var resourceFileContent = getResourceFileContent("arbeidsforhold_fra_aareg.json");
        JsonNode node = new ObjectMapper().readTree(resourceFileContent);

        var arbeidsforhold = mapToArbeidsforhold(node);

        assertThat(arbeidsforhold.getNavArbeidsforholdId(), equalTo(node.get("navArbeidsforholdId").asLong()));
        assertThat(arbeidsforhold.getArbeidsforholdId(), equalTo(node.get("arbeidsforholdId").asText()));
        assertThat(arbeidsforhold.getRegistrert(), equalTo(LocalDateTime.parse(node.get("registrert").asText())));
        assertThat(arbeidsforhold.getArbeidstaker().getOffentligIdent(), equalTo(node.get("arbeidstaker").get("offentligIdent").asText()));
        assertThat(arbeidsforhold.getArbeidstaker().getAktoerId(), equalTo(node.get("arbeidstaker").get("aktoerId").asText()));
        assertThat(((Organisasjon) arbeidsforhold.getArbeidsgiver()).getOrganisasjonsnummer(), equalTo(node.get("arbeidsgiver").get("organisasjonsnummer").asText()));
        assertThat(((Organisasjon) arbeidsforhold.getOpplysningspliktig()).getOrganisasjonsnummer(), equalTo(node.get("opplysningspliktig").get("organisasjonsnummer").asText()));
        assertThat(arbeidsforhold.getType(), equalTo(node.get("type").asText()));
        assertThat(arbeidsforhold.getInnrapportertEtterAOrdningen(), equalTo(node.get("innrapportertEtterAOrdningen").asBoolean()));
        assertThat(arbeidsforhold.getSistBekreftet(), equalTo(LocalDateTime.parse(node.get("sistBekreftet").asText())));
    }

    @Test
    public void shouldMapArbeidsforholdToRsArbeidsforhold() {
        var arbeidsforhold = buildArbeidsforhold();

        var rsArbeidsforhold = mapArbeidsforholdToRsArbeidsforhold(arbeidsforhold);

        assertThat(rsArbeidsforhold.getArbeidsforholdIDnav(), equalTo(arbeidsforhold.getNavArbeidsforholdId()));
        assertThat(rsArbeidsforhold.getArbeidsforholdID(), equalTo(arbeidsforhold.getArbeidsforholdId()));
        assertThat(rsArbeidsforhold.getArbeidstaker().getIdent(), equalTo(arbeidsforhold.getArbeidstaker().getOffentligIdent()));
        assertThat(((RsOrganisasjon) rsArbeidsforhold.getArbeidsgiver()).getOrgnummer(), equalTo(((Organisasjon) arbeidsforhold.getArbeidsgiver()).getOrganisasjonsnummer()));
        assertThat(rsArbeidsforhold.getArbeidsforholdstype(), equalTo(arbeidsforhold.getType()));
        assertThat(rsArbeidsforhold.getAnsettelsesPeriode().getFom(), equalTo(arbeidsforhold.getAnsettelsesperiode().getPeriode().getFom().atStartOfDay()));
        assertThat(rsArbeidsforhold.getArbeidsavtale().getYrke(), equalTo(arbeidsforhold.getArbeidsavtaler().get(0).getYrke()));
        assertThat(rsArbeidsforhold.getArbeidsavtale().getAvtaltArbeidstimerPerUke(), equalTo(arbeidsforhold.getArbeidsavtaler().get(0).getAntallTimerPrUke()));
        assertThat(rsArbeidsforhold.getArbeidsavtale().getStillingsprosent(), equalTo(arbeidsforhold.getArbeidsavtaler().get(0).getStillingsprosent()));
        assertThat(rsArbeidsforhold.getPermisjon().get(0).getPermisjonsprosent(), equalTo(arbeidsforhold.getPermisjonPermitteringer().get(0).getProsent()));
        assertThat(rsArbeidsforhold.getPermisjon().get(0).getPermisjonsPeriode().getFom(), equalTo(arbeidsforhold.getPermisjonPermitteringer().get(0).getPeriode().getFom().atStartOfDay()));
        assertThat(rsArbeidsforhold.getAntallTimerForTimeloennet().get(0).getAntallTimer(), equalTo(arbeidsforhold.getAntallTimerForTimeloennet().get(0).getAntallTimer()));
        assertThat(rsArbeidsforhold.getAntallTimerForTimeloennet().get(0).getPeriode().getFom(), equalTo(arbeidsforhold.getAntallTimerForTimeloennet().get(0).getPeriode().getFom().atStartOfDay()));
        assertThat(rsArbeidsforhold.getUtenlandsopphold().get(0).getLand(), equalTo(arbeidsforhold.getUtenlandsopphold().get(0).getLandkode()));
        assertThat(rsArbeidsforhold.getUtenlandsopphold().get(0).getPeriode().getFom(), equalTo(arbeidsforhold.getUtenlandsopphold().get(0).getPeriode().getFom().atStartOfDay()));
    }

    private Arbeidsforhold buildArbeidsforhold() {
        return Arbeidsforhold.builder()
                .navArbeidsforholdId(123L)
                .arbeidsforholdId("234")
                .arbeidstaker(Person.builder()
                        .offentligIdent("01010101010")
                        .build())
                .arbeidsgiver(Organisasjon.builder()
                        .organisasjonsnummer("123456789")
                        .build())
                .opplysningspliktig(Organisasjon.builder()
                        .organisasjonsnummer("123456789")
                        .build())
                .type("type")
                .ansettelsesperiode(Ansettelsesperiode.builder()
                        .periode(Periode.builder()
                                .fom(LocalDate.of(2020, 1, 1))
                                .build())
                        .build())
                .arbeidsavtaler(Collections.singletonList(Arbeidsavtale.builder()
                        .yrke("yrke")
                        .antallTimerPrUke(40.0)
                        .stillingsprosent(100.0)
                        .sporingsinformasjon(Sporingsinformasjon.builder()
                                .opprettetTidspunkt(LocalDateTime.now())
                                .endretTidspunkt(LocalDateTime.now())
                                .build())
                        .build()))
                .permisjonPermitteringer(Collections.singletonList(PermisjonPermittering.builder()
                        .prosent(100.0)
                        .periode(Periode.builder()
                                .fom(LocalDate.of(2020, 4, 1))
                                .build())
                        .build()))
                .antallTimerForTimeloennet(Collections.singletonList(AntallTimerForTimeloennet.builder()
                        .antallTimer(20.0)
                        .periode(Periode.builder()
                                .fom(LocalDate.of(2020, 5, 1))
                                .build())
                        .build()))
                .utenlandsopphold(Collections.singletonList(Utenlandsopphold.builder()
                        .landkode("land")
                        .periode(Periode.builder()
                                .fom(LocalDate.of(2020, 6, 1))
                                .build())
                        .build()))
                .innrapportertEtterAOrdningen(true)
                .build();
    }
}