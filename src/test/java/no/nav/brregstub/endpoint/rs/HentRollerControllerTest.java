package no.nav.brregstub.endpoint.rs;

import no.nav.brregstub.ApplicationConfig;
import no.nav.brregstub.api.OrganisasjonTo;
import no.nav.brregstub.database.domene.HentRolle;
import no.nav.brregstub.database.repository.HentRolleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
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
public class HentRollerControllerTest {

    public static final String API_V_1_ROLLER = "/api/v1/hentrolle/";
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private HentRolleRepository repository;

    @Test
    @DisplayName("GET rolle returnerer 404 hvis ikke eksisterer")
    public void skalKasteNotFoundHvisRolleIkkeEksister() {
        var response = restTemplate.getForEntity(API_V_1_ROLLER + "0",
                                                 String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET rolle returnerer 200 hvis ikke eksisterer")
    public void skalHenteRolleutskriftFraDatabase() {
        var nyRolle = new HentRolle();
        nyRolle.setOrgnr(1);
        nyRolle.setJson("{\"orgnr\": 1}");
        repository.save(nyRolle);

        var response = restTemplate.getForEntity(API_V_1_ROLLER + nyRolle.getOrgnr(),
                                                 OrganisasjonTo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrgnr()).isEqualTo(nyRolle.getOrgnr());
    }


    @Test
    @DisplayName("DELETE rolle skal slettes fra database")
    public void skalSletteRolleutskrift() {
        var rolleSomSkalSlettes = new HentRolle();
        rolleSomSkalSlettes.setOrgnr(3);
        rolleSomSkalSlettes.setJson("{}");
        repository.save(rolleSomSkalSlettes);

        var responseDelete =
                restTemplate.exchange(API_V_1_ROLLER + rolleSomSkalSlettes.getOrgnr(), HttpMethod.DELETE, null, String.class);
        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        var responseGet =
                restTemplate.getForEntity(API_V_1_ROLLER + rolleSomSkalSlettes.getOrgnr(), String.class);
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @DisplayName("POST rolle skal opprette ny databaseinnslag")
    public void skalLagreRequestIDatabase() {
        var to = new OrganisasjonTo();
        to.setOrgnr(4);
        to.setRegistreringsdato(LocalDate.now());


        var response =
                restTemplate.exchange(API_V_1_ROLLER, HttpMethod.POST, new HttpEntity<>(to), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("path")).isEqualTo("/api/v1/hentrolle/4");

    }

    @Test
    @DisplayName("POST rolle returnere bad request ved manglende feilt")
    public void skalReturnereBadRequestVedValideringsFeil() {
        var to = new OrganisasjonTo();

        var response =
                restTemplate.exchange(API_V_1_ROLLER, HttpMethod.POST, new HttpEntity<>(to), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
}
