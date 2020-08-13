package no.nav.brregstub.endpoint.rs.v2;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Collections;
import java.util.Map;

import no.nav.brregstub.ApplicationConfig;
import no.nav.brregstub.api.common.Egenskap;
import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.api.common.RsAdresse;
import no.nav.brregstub.api.common.RsNavn;
import no.nav.brregstub.api.v2.RsRolle;
import no.nav.brregstub.api.v2.RsRolleStatus;
import no.nav.brregstub.api.v2.RsRolleoversikt;
import no.nav.brregstub.database.domene.Rolleoversikt;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.brregstub.database.repository.RolleoversiktRepository;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { ApplicationConfig.class })
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RolleoversiktControllerTest {

    public static final String API_V_2_ROLLEUTSKRIFT = "/api/v2/rolleoversikt";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RolleoversiktRepository rolleoversiktRepository;

    @Autowired
    private HentRolleRepository rolleRepository;

    @Test
    @DisplayName("GET rolleoversikt returnerer 404 hvis ikke eksisterer")
    public void skalKasteNotFoundHvisRolleIkkeEksister() {
        var response = restTemplate.exchange(API_V_2_ROLLEUTSKRIFT,
                HttpMethod.GET,
                createHttpEntity("eksister ikke", null),
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 400 hvis input mangler")
    public void skalKasteBadRequestHvisInputMangler() {
        var response = restTemplate.getForEntity(API_V_2_ROLLEUTSKRIFT,
                Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message").toString()).contains("Missing request header 'Nav-Personident'");
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 200 hvis ikke eksisterer")
    public void skalHenteRolleutskriftFraDatabase() {
        var fnr = "03030303030";
        var nyRolle = new Rolleoversikt();
        nyRolle.setIdent(fnr);
        nyRolle.setJson("{\"fnr\":\"" + fnr + "\"}");
        rolleoversiktRepository.save(nyRolle);

        var response = restTemplate.exchange(API_V_2_ROLLEUTSKRIFT,
                HttpMethod.GET,
                createHttpEntity(fnr, null),
                RsRolleoversikt.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getFnr()).isEqualTo(fnr);
    }

    @Test
    @DisplayName("DELETE rolleoversikt skal slette rolleoversikt")
    public void skalSletteRolleutskrift() {
        var nyRolle = new Rolleoversikt();
        nyRolle.setIdent("slettes");
        nyRolle.setJson("{\"fnr\":\"slettes\"}");
        rolleoversiktRepository.save(nyRolle);

        var responseDelete =
                restTemplate.exchange(API_V_2_ROLLEUTSKRIFT, HttpMethod.DELETE, createHttpEntity("slettes", null), String.class);
        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        var responseGet =
                restTemplate.exchange(API_V_2_ROLLEUTSKRIFT, HttpMethod.GET, createHttpEntity("slettes", null), String.class);
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("DELETE rolleoversikt returnerer 400 hvis input mangler")
    public void deleteSkalKasteBadRequestHvisInputMangler() {
        var responseDelete =
                restTemplate.exchange(API_V_2_ROLLEUTSKRIFT, HttpMethod.DELETE, null, Map.class);
        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseDelete.getBody().get("message").toString()).contains("Missing request header 'Nav-Personident'");
    }

    @Test
    @DisplayName("POST rolleoversikt skal opprette ny rolleoversikt")
    public void skalLagreRequestIDatabase() {
        var fnr = "01010101010";
        var orgnummer = 99112345;
        var rsRolleoversikt = lagGyldigRsRolleoversikt(fnr, orgnummer);

        var response =
                restTemplate.exchange(API_V_2_ROLLEUTSKRIFT, HttpMethod.POST, createHttpEntity(fnr, rsRolleoversikt), RsRolleoversikt.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getFnr()).isEqualTo(fnr);
    }

    @Test
    @DisplayName("POST rolleoversikt skal returnere bad request ved mangle input")
    public void skalReturnereBadRequestVedPost() {
        var fnr = "01010101010";
        var rsRolleoversikt = new RsRolleoversikt();

        var response =
                restTemplate.exchange(API_V_2_ROLLEUTSKRIFT, HttpMethod.POST, createHttpEntity(fnr, rsRolleoversikt), RsRolleoversikt.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST rolleoversikt skal opprette ny databaseinnslag i organisasjonsbase")
    public void skalLagreOrganisasjonIDatabase() throws JsonProcessingException {
        var fnr = "02020202020";
        int orgnummer = 99012345;
        restTemplate.exchange(API_V_2_ROLLEUTSKRIFT, HttpMethod.POST, createHttpEntity(fnr, lagGyldigRsRolleoversikt(fnr, orgnummer)), RsRolleoversikt.class);

        var organisasjon = rolleRepository.findByOrgnr(orgnummer).orElse(null);
        assertThat(organisasjon).isNotNull();

        var databaseJson = new ObjectMapper().readTree(organisasjon.getJson());
        assertThat(databaseJson.get("orgnr").asInt()).isEqualTo(orgnummer);
        assertThat(databaseJson.get("deltakere").get("roller").get(0).get("fodselsnr").asText()).isEqualTo(fnr);
        assertThat(databaseJson.get("deltakere").get("roller").get(0).get("fratraadt").asBoolean()).isEqualTo(false);
    }

    private RsRolleoversikt lagGyldigRsRolleoversikt(
            String fnr,
            int orgnummer
    ) {
        var rsRolleoversikt = new RsRolleoversikt();
        rsRolleoversikt.setFnr(fnr);
        rsRolleoversikt.setHovedstatus(1);
        var rsNavn = new RsNavn();
        rsNavn.setNavn1("Navn");
        rsRolleoversikt.setNavn(rsNavn);
        var rsAdresse = new RsAdresse();
        rsAdresse.setAdresse1("Adresse 1");
        rsAdresse.setLandKode("NO");
        rsRolleoversikt.setAdresse(rsAdresse);
        var personRolle = new RsRolleStatus();
        personRolle.setEgenskap(Egenskap.Deltager);
        personRolle.setFratraadt(false);
        var enhet = new RsRolle();
        enhet.setRegistreringsdato(LocalDate.now());
        enhet.setOrgNr(orgnummer);
        enhet.setRolle(RolleKode.DELT);
        enhet.setPersonRolle(Collections.singletonList(personRolle));
        rsRolleoversikt.getEnheter().add(enhet);
        return rsRolleoversikt;
    }

    private HttpEntity createHttpEntity(
            String ident,
            RsRolleoversikt body
    ) {
        var headers = new HttpHeaders();
        headers.add("Nav-Personident", ident);
        return new HttpEntity(body, headers);
    }
}
