package no.nav.testnav.apps.adresseservice.mapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class MatrikkeladresseMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new MatrikkeladresseMappingStrategy());
    }

    @Test
    void mapMatrikkeladresseAlleFelter_OK() {

        var kilde = buildMatrikkeladresseDTO();

        var destinasjon = mapperFacade.map(kilde, MatrikkeladresseDTO.class);

        assertThat(destinasjon.getMatrikkelId(), is(equalTo("12345")));
        assertThat(destinasjon.getTilleggsnavn(), is(equalTo("Tilleggsnavn")));
        assertThat(destinasjon.getPostnummer(), is(equalTo("0123")));
        assertThat(destinasjon.getPoststed(), is(equalTo("Oslo")));
        assertThat(destinasjon.getKommunenummer(), is(equalTo("0301")));
        assertThat(destinasjon.getGaardsnummer(), is(equalTo("100")));
        assertThat(destinasjon.getBruksnummer(), is(equalTo("200")));
    }

    private static no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseDTO buildMatrikkeladresseDTO() {

        return no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseDTO.builder()
                .matrikkeladresse(no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseDTO.AdresseDTO.builder()
                        .id("12345")
                        .adressetilleggsnavn("Tilleggsnavn")
                        .postnummeromraade(no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseDTO.PostdataDTO.builder()
                                .postnummer("0123")
                                .poststed("Oslo")
                                .build())
                        .matrikkelenhet(no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseDTO.MatrikkelDTO.builder()
                                .matrikkelnummer(no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseDTO.MatrikkelnummerDTO.builder()
                                        .kommunenummer("0301")
                                        .gardsnummer("100")
                                        .bruksnummer("200")
                                        .build())
                                .build())
                        .build())
                .build();
    }
}