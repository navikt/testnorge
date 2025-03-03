package no.nav.brregstub.endpoint.rs.v1;

import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.api.common.UnderstatusKode;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KodeControllerTest {

    @Autowired
    protected TestRestTemplate restTemplate;


    @Test
    @DisplayName("GET kode/roller returnerer 200 og rollene")
    void skalHenteRollelist() {
        var response = restTemplate.getForEntity("/api/v1/kode/roller", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(RolleKode.SAM.name())).isEqualTo(RolleKode.SAM.getBeskrivelse());
    }


    @Test
    @DisplayName("GET kode/understatus returnerer 200 og tilgjenglige understatuser")
    void skalHenteUnderstatuser() {
        var response = restTemplate.getForEntity("/api/v1/kode/understatus", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get("1")).isEqualTo(UnderstatusKode.understatusKoder.get(1));
    }
}
