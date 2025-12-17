package no.nav.brregstub.endpoint.rs.v1;

import no.nav.brregstub.api.common.RsAdresse;
import no.nav.brregstub.api.common.RsNavn;
import no.nav.brregstub.api.v1.RolleTo;
import no.nav.brregstub.api.v1.RolleoversiktTo;
import no.nav.brregstub.database.domene.Rolleoversikt;
import no.nav.brregstub.database.repository.RolleoversiktRepository;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HentRolleoversiktControllerTest {

    public static final String API_V_1_ROLLEUTSKRIFT = "/api/v1/rolleoversikt";
    private static final String IDENT = "teste";
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private RolleoversiktRepository repository;

    private RolleoversiktTo lagGyldigRolleoversiktTo() {
        var to = new RolleoversiktTo();
        to.setFnr("ny");
        to.setHovedstatus(1);
        var navntTo = new RsNavn();
        navntTo.setNavn1("Navn");
        to.setNavn(navntTo);
        var adresseTo = new RsAdresse();
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

    private HttpEntity<RolleoversiktTo> createHttpEntity(
            String ident,
            RolleoversiktTo body
    ) {
        var headers = new HttpHeaders();
        headers.add("Nav-Personident", ident);
        return new HttpEntity<>(body, headers);
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 404 hvis ikke eksisterer")
    void skalKasteNotFoundHvisRolleIkkeEksister() {
        var response = restTemplate.exchange(API_V_1_ROLLEUTSKRIFT,
                HttpMethod.GET,
                createHttpEntity("eksister ikke", null),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 400 hvis input mangler")
    void skalKasteBadRequestHvisInputMangler() {
        var response = restTemplate.getForEntity(API_V_1_ROLLEUTSKRIFT, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 200 hvis ikke eksisterer")
    void skalHenteRolleutskriftFraDatabase() {
        var nyRolle = new Rolleoversikt();
        nyRolle.setIdent(IDENT);
        nyRolle.setJson("{\"fnr\":\"ident\"}");
        Mockito.when(repository.findByIdent("ident")).thenReturn(Optional.of(nyRolle));

        var response = restTemplate.exchange(API_V_1_ROLLEUTSKRIFT,
                HttpMethod.GET,
                createHttpEntity("ident", null),
                RolleoversiktTo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFnr()).isEqualTo("ident");
    }

    @Test
    @DisplayName("DELETE rolleoversikt skal slette rolleoversikt")
    void skalSletteRolleutskrift() {
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
    void deleteSkalKasteBadRequestHvisInputMangler() {
        var responseDelete =
                restTemplate.exchange(API_V_1_ROLLEUTSKRIFT, HttpMethod.DELETE, null, Map.class);
        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST rolleoversikt skal opprette ny rolleoversikt")
    void skalLagreRequestIDatabase() {
        RolleoversiktTo to = lagGyldigRolleoversiktTo();

        var response =
                restTemplate.exchange(API_V_1_ROLLEUTSKRIFT, HttpMethod.POST, createHttpEntity("ny", to), RolleoversiktTo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFnr()).isEqualTo("ny");

    }

    @Test
    @DisplayName("POST rolleoversikt skal returnere bad request ved mangle input")
    void skalReturnereBadRequestVedPost() {
        RolleoversiktTo to = new RolleoversiktTo();

        var response =
                restTemplate.exchange(API_V_1_ROLLEUTSKRIFT, HttpMethod.POST, createHttpEntity("ny", to), RolleoversiktTo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
}
