package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.rtv.namespacetps.PersonIdentType;
import no.rtv.namespacetps.PersonType;
import no.rtv.namespacetps.TpsPersonDokumentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    private TpsPersonDokumentType tpsPersonDokument;
    private String fnr = "01010101010";

    @Before
    public void setUp() {
        PersonIdentType personIdent = new PersonIdentType();
        personIdent.setPersonIdent(fnr);
        // Person person = Person.builder()
        // .personIdent(new ArrayList<>(Collections.singletonList(personIdent)))
        // .build();
        tpsPersonDokument = new TpsPersonDokumentType();
        PersonType person = new PersonType();
        tpsPersonDokument.setPerson(person);
        // tpsPersonDokument = TpsPersonDokument.builder()
        // .person(person)
        // .build();
    }

    @Ignore
    @Test
    public void shouldSendPersondokumentTilHodejegeren() {
        stubHodejegerenConsumer();

        List<String> identer = hodejegerenConsumer.sendTpsPersondokumentTilHodejegeren(tpsPersonDokument, fnr);

        assertThat(identer, contains(fnr));
    }

    private void stubHodejegerenConsumer() {
        stubFor(post(urlPathEqualTo("/hodejegeren/api/v1/historikk/skd/oppdaterDokument/" + fnr))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + fnr + "\"]")));
    }
}