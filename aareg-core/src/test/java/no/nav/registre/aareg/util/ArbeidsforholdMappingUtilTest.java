package no.nav.registre.aareg.util;

import static no.nav.registre.aareg.testutils.ResourceUtils.getResourceFileContent;
import static no.nav.registre.aareg.util.ArbeidsforholdMappingUtil.mapToArbeidsforhold;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.time.LocalDateTime;

import no.nav.tjenester.aordningen.arbeidsforhold.v1.Organisasjon;

@RunWith(MockitoJUnitRunner.class)
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

    }
}