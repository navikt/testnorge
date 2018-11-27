package no.nav.identpool.navnepool.rs.v1;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.identpool.ComponentTestConfig;
import no.nav.identpool.navnepool.domain.Navn;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ComponentTestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FiktiveNavnComponentTest {

    private static final ParameterizedTypeReference<List<Navn>> RESPONSE_TYPE = new ParameterizedTypeReference<List<Navn>>() {
    };
    @Autowired
    protected TestRestTemplate testRestTemplate;
    @Autowired
    private List<String> validFornavn;
    @Autowired
    private List<String> validEtternavn;

    /**
     * Testscenario: NÅR endepunktet fiktive-navn/tilfeldig og forespør et gitt antall navn, så skal responsen
     * inneholde det antallet med fiktive navn (adjektiv-fornavn og substantiv-etternavn).
     *
     * @throws URISyntaxException
     */
    @Test
    public void happypathTestHentTilfeldigeNavn() {

        RequestEntity httpEntity = RequestEntity.get(URI.create("/api/v1/fiktive-navn/tilfeldig?antall=3")).build();
        List<Navn> navneliste = testRestTemplate.exchange(httpEntity, RESPONSE_TYPE).getBody();

        assertThat(navneliste.size(), is(3));
        List<String> responseFornavn = navneliste.stream().map(Navn::getFornavn).collect(Collectors.toList());
        List<String> responseEtternavn = navneliste.stream().map(Navn::getEtternavn).collect(Collectors.toList());
        assertTrue(validFornavn.containsAll(responseFornavn));
        assertTrue(validEtternavn.containsAll(responseEtternavn));
    }

    /**
     * Testscenario: når endepunktet valider blir kalt med navn, så skal den validere navnet opp mot gyldige lister av testnavn.
     * Her testes spesifikt at fornavn som ikke er i listen, ikke valideres.
     *
     * Flere scenarier blir testet på enhetstest-nivå: NavnepoolServiceValideringTest
     */
    @Test
    public void validereNavn() {
        String tullenavn = "tullenavn";
        assertTrue(!validFornavn.contains(tullenavn));

        Boolean validert = testRestTemplate.getForObject("/api/v1/fiktive-navn/valider?fornavn="+tullenavn, Boolean.class);
        assertFalse(validert);
    }
}
