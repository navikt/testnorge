package no.nav.dolly.proxy.config;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
class ConsumersTest {

    @Autowired
    private Consumers consumers;

    @Test
    void shouldKeepTokenScopesForAllTargets() {
        var actual = new LinkedHashMap<String, String>();
        actual.put("aareg-services-q1", scope(consumers.getAaregServicesQ1()));
        actual.put("aareg-services-q2", scope(consumers.getAaregServicesQ2()));
        actual.put("aareg-services-q4", scope(consumers.getAaregServicesQ4()));
        actual.put("aareg-vedlikehold-q1", scope(consumers.getAaregVedlikeholdQ1()));
        actual.put("aareg-vedlikehold-q2", scope(consumers.getAaregVedlikeholdQ2()));
        actual.put("aareg-vedlikehold-q4", scope(consumers.getAaregVedlikeholdQ4()));
        actual.put("dokarkiv-q1", scope(consumers.getDokarkivQ1()));
        actual.put("dokarkiv-q2", scope(consumers.getDokarkivQ2()));
        actual.put("dokarkiv-q4", scope(consumers.getDokarkivQ4()));
        actual.put("fullmakt", scope(consumers.getFullmakt()));
        actual.put("henvendelse", scope(consumers.getHenvendelse()));
        actual.put("inst", scope(consumers.getInst()));
        actual.put("kelvin-aap", scope(consumers.getKelvinAap()));
        actual.put("kontoregister", scope(consumers.getKontoregister()));
        actual.put("krrstub", scope(consumers.getKrrstub()));
        actual.put("medl", scope(consumers.getMedl()));
        actual.put("oppfoelging", scope(consumers.getOppfoelging()));
        actual.put("oppfoelgingsvedtak14a", scope(consumers.getOppfoelgingsvedtak14a()));
        actual.put("pdl-api-q1", scope(consumers.getPdlApiQ1()));
        actual.put("pdl-api-q2", scope(consumers.getPdlApiQ2()));
        actual.put("pdl-testdata", scope(consumers.getPdlTestdata()));
        actual.put("pensjon-afp-q1", scope(consumers.getPensjonAfpQ1()));
        actual.put("pensjon-afp-q2", scope(consumers.getPensjonAfpQ2()));
        actual.put("pensjon-samboer-q1", scope(consumers.getPensjonSamboerQ1()));
        actual.put("pensjon-samboer-q2", scope(consumers.getPensjonSamboerQ2()));
        actual.put("saf-q1", scope(consumers.getSafQ1()));
        actual.put("saf-q2", scope(consumers.getSafQ2()));
        actual.put("saf-q4", scope(consumers.getSafQ4()));
        actual.put("skattekort-q1", scope(consumers.getSkattekortQ1()));
        actual.put("skattekort-q2", scope(consumers.getSkattekortQ2()));
        actual.put("skjermingsregister", scope(consumers.getSkjermingsregister()));
        actual.put("sykemelding", scope(consumers.getSykemelding()));
        actual.put("udistub", scope(consumers.getUdistub()));

        assertThat(actual).containsExactlyEntriesOf(expectedScopes());
    }

    @Test
    void shouldHaveNameAndExternalNamespaceForAllTargets() throws IllegalAccessException {
        var ownTargets = Set.of("brregstub", "udistub");
        var fields = Arrays.stream(Consumers.class.getDeclaredFields())
                .filter(field -> field.getType() == ServerProperties.class)
                .toList();
        assertThat(fields).isNotEmpty();

        for (var field : fields) {
            field.setAccessible(true);
            var serverProperties = (ServerProperties) field.get(consumers);
            assertThat(serverProperties).as(field.getName()).isNotNull();
            assertThat(serverProperties.getUrl()).as(field.getName() + ".url").isNotBlank();
            assertThat(serverProperties.getName()).as(field.getName() + ".name").isNotBlank();
            if (ownTargets.contains(field.getName())) {
                assertThat(serverProperties.getNamespace()).as(field.getName() + ".namespace").isEqualTo("dolly");
            } else {
                assertThat(serverProperties.getNamespace()).as(field.getName() + ".namespace").isNotEqualTo("dolly");
            }
        }
    }

    private static String scope(ServerProperties serverProperties) {
        return serverProperties.toAzureAdScope();
    }

    private static Map<String, String> expectedScopes() {
        var expected = new LinkedHashMap<String, String>();
        expected.put("aareg-services-q1", "api://dev-fss.arbeidsforhold.aareg-services-nais-q1/.default");
        expected.put("aareg-services-q2", "api://dev-fss.arbeidsforhold.aareg-services-nais/.default");
        expected.put("aareg-services-q4", "api://dev-fss.arbeidsforhold.aareg-services-nais-q4/.default");
        expected.put("aareg-vedlikehold-q1", "api://dev-fss.arbeidsforhold.aareg-dolly-api-q1/.default");
        expected.put("aareg-vedlikehold-q2", "api://dev-fss.arbeidsforhold.aareg-dolly-api-q2/.default");
        expected.put("aareg-vedlikehold-q4", "api://dev-fss.arbeidsforhold.aareg-dolly-api-q4/.default");
        expected.put("dokarkiv-q1", "api://dev-fss.teamdokumenthandtering.dokarkiv-q1/.default");
        expected.put("dokarkiv-q2", "api://dev-fss.teamdokumenthandtering.dokarkiv/.default");
        expected.put("dokarkiv-q4", "api://dev-fss.teamdokumenthandtering.dokarkiv-q4/.default");
        expected.put("fullmakt", "api://dev-gcp.repr.fullmakt/.default");
        expected.put("henvendelse", "api://dev-gcp.teamnks.sf-henvendelse-api-proxy/.default");
        expected.put("inst", "api://dev-fss.team-rocket.opphold-testdata/.default");
        expected.put("kelvin-aap", "api://dev-gcp.aap.behandlingsflyt/.default");
        expected.put("kontoregister", "api://dev-gcp.okonomi.sokos-kontoregister-person/.default");
        expected.put("krrstub", "api://dev-gcp.team-rocket.digdir-krr-stub/.default");
        expected.put("medl", "api://dev-gcp.team-rocket.medlemskap-medl-testdata/.default");
        expected.put("oppfoelging", "api://dev-gcp.poao.veilarboppfolging/.default");
        expected.put("oppfoelgingsvedtak14a", "api://dev-gcp.obo.veilarbvedtaksstotte/.default");
        expected.put("pdl-api-q1", "api://dev-fss.pdl.pdl-api-q1/.default");
        expected.put("pdl-api-q2", "api://dev-fss.pdl.pdl-api/.default");
        expected.put("pdl-testdata", "api://dev-fss.pdl.pdl-testdata/.default");
        expected.put("pensjon-afp-q1", "api://dev-gcp.pensjon-saksbehandling.pensjon-afp-offentlig-mock-q1/.default");
        expected.put("pensjon-afp-q2", "api://dev-gcp.pensjon-saksbehandling.pensjon-afp-offentlig-mock-q2/.default");
        expected.put("pensjon-samboer-q1", "api://dev-gcp.pensjon-person.pensjon-samboerforhold-backend-q1/.default");
        expected.put("pensjon-samboer-q2", "api://dev-gcp.pensjon-person.pensjon-samboerforhold-backend-q2/.default");
        expected.put("saf-q1", "api://dev-fss.teamdokumenthandtering.saf-q1/.default");
        expected.put("saf-q2", "api://dev-fss.teamdokumenthandtering.saf/.default");
        expected.put("saf-q4", "api://dev-fss.teamdokumenthandtering.saf-q4/.default");
        expected.put("skattekort-q1", "api://dev-gcp.okonomi.sokos-skattekort-q1/.default");
        expected.put("skattekort-q2", "api://dev-gcp.okonomi.sokos-skattekort/.default");
        expected.put("skjermingsregister", "api://dev-fss.nom.skjermede-personer/.default");
        expected.put("sykemelding", "api://dev-gcp.tsm.tsm-input-dolly/.default");
        expected.put("udistub", "api://dev-fss.dolly.testnav-udi-stub/.default");
        return expected;
    }

}
