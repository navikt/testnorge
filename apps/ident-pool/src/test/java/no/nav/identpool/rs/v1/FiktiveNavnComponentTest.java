package no.nav.identpool.rs.v1;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.domain.Navn;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FiktiveNavnComponentTest extends ComponentTestbase {

    /**
     * Testscenario: NÅR endepunktet fiktive-navn/tilfeldig og forespør et gitt antall navn, så skal responsen
     * inneholde det antallet med fiktive navn (adjektiv-fornavn og substantiv-etternavn).
     *
     * @throws URISyntaxException
     */
    @Test
    void happypathTestHentTilfeldigeNavn() {
        RequestEntity httpEntity = RequestEntity.get(URI.create("/api/v1/fiktive-navn/tilfeldig?antall=3")).build();
        List<Navn> navneliste = testRestTemplate.exchange(httpEntity, new ParameterizedTypeReference<List<Navn>>(){}).getBody();

        assertThat(navneliste.size(), is(3));
    }

    /**
     * Tester at service blir kalt med antall default antall navn som input NÅR REST-endepunktet blir kalt.
     * Antall har default verdi 1.
     */
    @Test
    void hentEttTilfeldigNavn() {
        List navneliste = doGetRequest(URI.create("/api/v1/fiktive-navn/tilfeldig"), createBodyEntity(""), List.class).getBody();
        assertThat(navneliste.size(), is(1));
    }

    /**
     * Testscenario: når endepunktet valider blir kalt med navn, så skal den validere navnet opp mot gyldige lister av testnavn.
     * Her testes spesifikt at fornavn som ikke er i listen, ikke valideres.
     *
     * Flere scenarier blir testet på enhetstest-nivå: NavnepoolServiceValideringTest
     */
    @Test
    void validereNavn() {
        String tullenavn = "tullenavn";
        Boolean validert = doGetRequest(URI.create("/api/v1/fiktive-navn/valider?fornavn="+tullenavn), createBodyEntity(""), Boolean.class).getBody();

        assertFalse(validert);
    }
}
