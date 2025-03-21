package no.nav.brregstub.endpoint.rs.v1;

import no.nav.brregstub.api.common.RsOrganisasjon;
import no.nav.brregstub.database.domene.HentRolle;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HentRollerControllerTest {

    public static final String API_V_1_ROLLER = "/api/v1/hentrolle";
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private HentRolleRepository repository;

    @Test
    @DisplayName("GET rolle returnerer 404 hvis ikke eksisterer")
    void skalKasteNotFoundHvisRolleIkkeEksister() {
        var response = restTemplate.getForEntity(API_V_1_ROLLER + "0",
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET rolle returnerer 200 hvis ikke eksisterer")
    void skalHenteRolleutskriftFraDatabase() {
        var nyRolle = new HentRolle();
        nyRolle.setOrgnr(1);
        nyRolle.setJson("{\"orgnr\": 1}");
        Mockito.when(repository.findByOrgnr(1)).thenReturn(Optional.of(nyRolle));

        var response = restTemplate.getForEntity(API_V_1_ROLLER + "/" + nyRolle.getOrgnr(),
                RsOrganisasjon.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrgnr()).isEqualTo(nyRolle.getOrgnr());
    }


    @Test
    @DisplayName("DELETE rolle skal slettes fra database")
    void skalSletteRolleutskrift() {
        var rolleSomSkalSlettes = new HentRolle();
        rolleSomSkalSlettes.setOrgnr(3);
        rolleSomSkalSlettes.setJson("{}");
        Mockito.when(repository.findByOrgnr(3)).thenReturn(Optional.of(rolleSomSkalSlettes));

        var responseDelete =
                restTemplate.exchange(API_V_1_ROLLER + "/" + rolleSomSkalSlettes.getOrgnr(), HttpMethod.DELETE, null, String.class);
        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.OK);

        Mockito.when(repository.findByOrgnr(3)).thenReturn(Optional.empty());
        var responseGet =
                restTemplate.getForEntity(API_V_1_ROLLER + rolleSomSkalSlettes.getOrgnr(), String.class);
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @DisplayName("POST rolle skal opprette ny databaseinnslag")
    void skalLagreRequestIDatabase() {
        var to = new RsOrganisasjon();
        to.setOrgnr(4);
        to.setRegistreringsdato(LocalDate.now());


        var response =
                restTemplate.exchange(API_V_1_ROLLER, HttpMethod.POST, new HttpEntity<>(to), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("path")).isEqualTo("/api/v1/hentrolle/4");

    }

    @Test
    @DisplayName("POST rolle returnere bad request ved manglende feilt")
    void skalReturnereBadRequestVedValideringsFeil() {
        var to = new RsOrganisasjon();

        var response =
                restTemplate.exchange(API_V_1_ROLLER, HttpMethod.POST, new HttpEntity<>(to), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
}
