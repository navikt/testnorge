package no.nav.brregstub.endpoint.rs;

import no.nav.brregstub.ApplicationConfig;
import no.nav.brregstub.api.AdresseTo;
import no.nav.brregstub.api.NavnTo;
import no.nav.brregstub.api.RolleTo;
import no.nav.brregstub.api.RolleoversiktTo;
import no.nav.brregstub.database.domene.Rolleoversikt;
import no.nav.brregstub.database.repository.RolleoversiktRepository;
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

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HentRolleoversiktControllerTest {

    public static final String API_V_1_ROLLEUTSKRIFT = "/api/v1/rolleoversikt";
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RolleoversiktRepository repository;

    @Test
    @DisplayName("GET rolleoversikt returnerer 404 hvis ikke eksisterer")
    public void skalKasteNotFoundHvisRolleIkkeEksister() {
        var response = restTemplate.exchange(API_V_1_ROLLEUTSKRIFT,
                                             HttpMethod.GET,
                                             createHttpEntity("eksister ikke", null),
                                             String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 400 hvis input mangler")
    public void skalKasteBadRequestHvisInputMangler() {
        var response = restTemplate.getForEntity(API_V_1_ROLLEUTSKRIFT,
                                                 Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message").toString()).contains("Missing request header 'Nav-Personident'");
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 200 hvis ikke eksisterer")
    public void skalHenteRolleutskriftFraDatabase() {
        var nyRolle = new Rolleoversikt();
        nyRolle.setIdent("ident");
        nyRolle.setJson("{\"fnr\":\"ident\"}");
        repository.save(nyRolle);

        var response = restTemplate.exchange(API_V_1_ROLLEUTSKRIFT,
                                             HttpMethod.GET,
                                             createHttpEntity("ident", null),
                                             RolleoversiktTo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getFnr()).isEqualTo("ident");
    }


    @Test
    @DisplayName("DELETE rolleoversikt skal slette rolleoversikt")
    public void skalSletteRolleutskrift() {
        var nyRolle = new Rolleoversikt();
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
    @DisplayName("DELETE rolleoversikt returnerer 400 hvis input mangler")
    public void deleteSkalKasteBadRequestHvisInputMangler() {
        var responseDelete =
                restTemplate.exchange(API_V_1_ROLLEUTSKRIFT, HttpMethod.DELETE, null, Map.class);
        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseDelete.getBody().get("message").toString()).contains("Missing request header 'Nav-Personident'");
    }


    @Test
    @DisplayName("POST rolleoversikt skal opprette ny rolleoversikt")
    public void skalLagreRequestIDatabase() {
        RolleoversiktTo to = lagGyldigRolleoversiktTo();

        var response =
                restTemplate.exchange(API_V_1_ROLLEUTSKRIFT, HttpMethod.POST, createHttpEntity("ny", to), RolleoversiktTo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getFnr()).isEqualTo("ny");

    }

    @Test
    @DisplayName("POST rolleoversikt skal returnere bad request ved mangle input")
    public void skalReturnereBadRequestVedPost() {
        RolleoversiktTo to = new RolleoversiktTo();

        var response =
                restTemplate.exchange(API_V_1_ROLLEUTSKRIFT, HttpMethod.POST, createHttpEntity("ny", to), RolleoversiktTo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    private RolleoversiktTo lagGyldigRolleoversiktTo() {
        var to = new RolleoversiktTo();
        to.setFnr("ny");
        to.setHovedstatus(1);
        var navntTo = new NavnTo();
        navntTo.setNavn1("Navn");
        to.setNavn(navntTo);
        var adresseTo = new AdresseTo();
        adresseTo.setAdresse1("Adresse 1");
        adresseTo.setLandKode("NO");
        to.setAdresse(adresseTo);
        var enhet = new RolleTo();
        enhet.setRegistreringsdato(LocalDate.now());
        enhet.setOrgNr(99887765);
        enhet.setRollebeskrivelse("beskrivelse");
        to.getEnheter().add(enhet);
        return to;
    }

    private HttpEntity createHttpEntity(String ident, RolleoversiktTo body) {
        var headers = new HttpHeaders();
        headers.add("Nav-Personident", ident);
        return new HttpEntity(body, headers);
    }
}
