package no.nav.brregstub.endpoint.rs.v1;

import no.nav.brregstub.ApplicationConfig;
import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.api.common.UnderstatusKode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KodeControllerTest {

    @Autowired
    protected TestRestTemplate restTemplate;


    @Test
    @DisplayName("GET kode/roller returnerer 200 og rollene")
    public void skalHenteRollelist() {
        var response = restTemplate.getForEntity("/api/v1/kode/roller", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(RolleKode.SAM.name())).isEqualTo(RolleKode.SAM.getBeskrivelse());
    }


    @Test
    @DisplayName("GET kode/understatus returnerer 200 og tilgjenglige understatuser")
    public void skalHenteUnderstatuser() {
        var response = restTemplate.getForEntity("/api/v1/kode/understatus", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get("1")).isEqualTo(UnderstatusKode.understatusKoder.get(1));
    }
}
