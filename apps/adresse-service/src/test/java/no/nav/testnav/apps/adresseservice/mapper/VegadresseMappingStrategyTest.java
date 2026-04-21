package no.nav.testnav.apps.adresseservice.mapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class VegadresseMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new VegadresseMappingStrategy());
    }

    @Test
    void mapVegadresseAlleFelter_OK() {

        var kilde = buildVegadresseDTO();

        var destinasjon = mapperFacade.map(kilde, VegadresseDTO.class);

        assertThat(destinasjon.getMatrikkelId(), is(equalTo("12345")));
        assertThat(destinasjon.getTilleggsnavn(), is(equalTo("Tilleggsnavn")));
        assertThat(destinasjon.getPostnummer(), is(equalTo("0123")));
        assertThat(destinasjon.getPoststed(), is(equalTo("Oslo")));
        assertThat(destinasjon.getKommunenummer(), is(equalTo("0301")));
        assertThat(destinasjon.getKommunenavn(), is(equalTo("Oslo")));
        assertThat(destinasjon.getAdressenavn(), is(equalTo("Karl Johans gate")));
        assertThat(destinasjon.getAdressekode(), is(equalTo("A123")));
        assertThat(destinasjon.getHusnummer(), is(equalTo(1001)));
        assertThat(destinasjon.getHusbokstav(), is(equalTo("A")));
        assertThat(destinasjon.getFylkesnummer(), is(equalTo("03")));
        assertThat(destinasjon.getFylkesnavn(), is(equalTo("Oslo")));
        assertThat(destinasjon.getBydelsnummer(), is(equalTo("123456")));
        assertThat(destinasjon.getBydelsnavn(), is(equalTo("Sagene")));
    }

    private static no.nav.testnav.apps.adresseservice.dto.VegadresseDTO buildVegadresseDTO() {

        return no.nav.testnav.apps.adresseservice.dto.VegadresseDTO.builder()
                .vegadresse(no.nav.testnav.apps.adresseservice.dto.VegadresseDTO.AdresseDTO.builder()
                        .id("12345")
                        .adressetilleggsnavn("Tilleggsnavn")
                        .postnummeromraade(no.nav.testnav.apps.adresseservice.dto.VegadresseDTO.PostdataDTO.builder()
                                .postnummer("0123")
                                .poststed("Oslo")
                                .build())
                        .veg(no.nav.testnav.apps.adresseservice.dto.VegadresseDTO.VegDTO.builder()
                                .kommune(no.nav.testnav.apps.adresseservice.dto.VegadresseDTO.KommuneDTO.builder()
                                        .kommunenummer("0301")
                                        .kommunenavn("Oslo")
                                        .fylke(no.nav.testnav.apps.adresseservice.dto.VegadresseDTO.FylkeDTO.builder()
                                                .fylkesnummer("03")
                                                .fylkesnavn("Oslo")
                                                .build())
                                        .build())
                                .adressekode("A123")
                                .adressenavn("Karl Johans gate")
                                .kortAdressenavn("Karl Johan")
                                .stedsnummer("1001")
                                .build())
                        .nummer(1001)
                        .bokstav("A")
                        .bydel(no.nav.testnav.apps.adresseservice.dto.VegadresseDTO.BydelDTO.builder()
                                .bydelsnavn("Sagene")
                                .bydelsnummer("123456")
                                .build())
                        .build())
                .build();
    }
}