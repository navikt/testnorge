package no.nav.registre.sdForvalter.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

import no.nav.registre.sdForvalter.consumer.rs.request.ereg.EregMapperRequest;
import no.nav.registre.sdForvalter.consumer.rs.request.ereg.Navn;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class EregTest {

    @Test
    public void should_construct_ereg_from_eregmapperrequest() {
        Ereg ereg = Ereg.builder()
                .orgnr("999999999")
                .enhetstype("BEDR")
                .navn("NAVN")
                .redigertNavn("ET ANNET NAVN")
                .gruppe("WIP")
                .opprinnelse("Brreg")
                .build();

        EregMapperRequest eregMapperRequest = EregMapperRequest.builder()
                .orgnr("999999999")
                .enhetstype("BEDR")
                .navn(Navn.builder().redNavn("ET ANNET NAVN").navneListe(Collections.singletonList("NAVN")).build())
                .build();
        Ereg eregFromEregMapperRequest = new Ereg(eregMapperRequest);

        assertThat(ereg).isEqualToComparingFieldByField(eregFromEregMapperRequest);
    }
}
