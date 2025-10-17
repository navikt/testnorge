package no.nav.brregstub.endpoint.rs.v2;

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
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RolleoversiktControllerTest {

    public static final String API_V_2_ROLLEUTSKRIFT = "/api/v2/rolleoversikt";

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private RolleoversiktRepository rolleoversiktRepository;

    @MockitoBean
    private HentRolleRepository rolleRepository;

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
        personRolle.setEgenskap(Egenskap.DELTAGER);
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

    @Test
    @DisplayName("GET rolleoversikt returnerer 404 hvis ikke eksisterer")
    void skalKasteNotFoundHvisRolleIkkeEksister() {
        var response = restTemplate.exchange(API_V_2_ROLLEUTSKRIFT,
                HttpMethod.GET,
                createHttpEntity("eksister ikke", null),
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 400 hvis input mangler")
    void skalKasteBadRequestHvisInputMangler() {
        var response = restTemplate.getForEntity(API_V_2_ROLLEUTSKRIFT, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 200 hvis ikke eksisterer")
    void skalHenteRolleutskriftFraDatabase() {
        var fnr = "03030303030";
        var nyRolle = new Rolleoversikt();
        nyRolle.setIdent(fnr);
        nyRolle.setJson("{\"fnr\":\"" + fnr + "\"}");
        Mockito.when(rolleoversiktRepository.findByIdent(nyRolle.getIdent())).thenReturn(Optional.of(nyRolle));

        var response = restTemplate.exchange(API_V_2_ROLLEUTSKRIFT,
                HttpMethod.GET,
                createHttpEntity(fnr, null),
                RsRolleoversikt.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getFnr()).isEqualTo(fnr);
    }

    @Test
    @DisplayName("DELETE rolleoversikt skal slette rolleoversikt")
    void skalSletteRolleutskrift() {
        var nyRolle = new Rolleoversikt();
        nyRolle.setIdent("slettes");
        nyRolle.setJson("{\"fnr\":\"slettes\"}");
        Mockito.when(rolleoversiktRepository.findByIdent(nyRolle.getIdent())).thenReturn(Optional.of(nyRolle));

        var responseDelete =
                restTemplate.exchange(API_V_2_ROLLEUTSKRIFT, HttpMethod.DELETE, createHttpEntity("slettes", null), String.class);
        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.OK);

        Mockito.when(rolleoversiktRepository.findByIdent(nyRolle.getIdent())).thenReturn(Optional.empty());

        var responseGet =
                restTemplate.exchange(API_V_2_ROLLEUTSKRIFT, HttpMethod.GET, createHttpEntity("slettes", null), String.class);
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("DELETE rolleoversikt returnerer 400 hvis input mangler")
    void deleteSkalKasteBadRequestHvisInputMangler() {
        var responseDelete =
                restTemplate.exchange(API_V_2_ROLLEUTSKRIFT, HttpMethod.DELETE, null, Map.class);
        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST rolleoversikt skal opprette ny rolleoversikt")
    void skalLagreRequestIDatabase() {
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
    void skalReturnereBadRequestVedPost() {
        var fnr = "01010101010";
        var rsRolleoversikt = new RsRolleoversikt();

        var response =
                restTemplate.exchange(API_V_2_ROLLEUTSKRIFT, HttpMethod.POST, createHttpEntity(fnr, rsRolleoversikt), RsRolleoversikt.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
