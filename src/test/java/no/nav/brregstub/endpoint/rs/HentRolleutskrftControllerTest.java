package no.nav.brregstub.endpoint.rs;

import no.nav.brregstub.ApplicationConfig;
import no.nav.brregstub.api.RolleutskriftTo;
import no.nav.brregstub.database.domene.Rolleutskrift;
import no.nav.brregstub.database.repository.RolleutskriftRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HentRolleutskrftControllerTest {

    public static final String API_V_1_ROLLEUTSKRIFT = "/api/v1/rolleutskrift";
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RolleutskriftRepository repository;

    @Test
    @DisplayName("GET rolleutskrift returnerer 404 hvis ikke eksisterer")
    public void skalKasteNotFoundHvisRolleIkkeEksister() {
        var response = restTemplate.exchange(API_V_1_ROLLEUTSKRIFT,
                                             HttpMethod.GET,
                                             createHttpEntity("eksister ikke", null),
                                             String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET rolleutskrift returnerer 200 hvis ikke eksisterer")
    public void skalHenteRolleutskriftFraDatabase() {
        var nyRolle = new Rolleutskrift();
        nyRolle.setIdent("ident");
        nyRolle.setJson("{\"fnr\":\"ident\"}");
        repository.save(nyRolle);

        var response = restTemplate.exchange(API_V_1_ROLLEUTSKRIFT,
                                             HttpMethod.GET,
                                             createHttpEntity("ident", null),
                                             RolleutskriftTo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getFnr()).isEqualTo("ident");
    }


    @Test
    @DisplayName("DELETE rolleutskrift skal slette rolleutskrift")
    public void skalSletteRolleutskrift() {
        var nyRolle = new Rolleutskrift();
        nyRolle.setIdent("slettes");
        nyRolle.setJson("{\"fnr\":\"slettes\"}");
        repository.save(nyRolle);

        var responseDelete =
                restTemplate.exchange(API_V_1_ROLLEUTSKRIFT, HttpMethod.DELETE, createHttpEntity("slettes", null), String.class);
        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        var responseGet =
                restTemplate.exchange(API_V_1_ROLLEUTSKRIFT, HttpMethod.GET, createHttpEntity("slettes", null), String.class);
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @DisplayName("POST rolleutskrift skal opprette ny rolleutskrift")
    public void skalLagreRequestIDatabase() {
        var to = new RolleutskriftTo();
        to.setFnr("ny");
        to.setHovedstatus(1);

        var response =
                restTemplate.exchange(API_V_1_ROLLEUTSKRIFT, HttpMethod.POST, createHttpEntity("ny", to), RolleutskriftTo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getFnr()).isEqualTo("ny");

    }

    private HttpEntity createHttpEntity(String ident, RolleutskriftTo body) {
        var headers = new HttpHeaders();
        headers.add("Nav-Personident", ident);
        return new HttpEntity(body, headers);
    }
}
