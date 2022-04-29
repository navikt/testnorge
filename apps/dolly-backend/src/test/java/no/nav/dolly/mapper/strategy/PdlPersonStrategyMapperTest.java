package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoMatrikkeladresse;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class PdlPersonStrategyMapperTest {

    private static final String ADRESSEKODE = "Gategaten 1";
    private static final String KOMMUNENUMMER = "1234";

    private MapperFacade mapperFacade;

    @BeforeEach
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(), new PdlPersonStrategyMapper());
    }

    @Test
    public void vegadresseMapping_OK() {

        BoGateadresse boGateadresse = mapperFacade.map(VegadresseDTO.builder().adressekode(ADRESSEKODE).build(), BoGateadresse.class);

        assertThat(boGateadresse.isGateadresse(), is(equalTo(true)));
        assertThat(boGateadresse.isMatrikkeladresse(), is(equalTo(false)));
        assertThat(boGateadresse.getGatekode(), is(equalTo(ADRESSEKODE)));
    }

    @Test
    public void matrikkeladresseMapping_OK() {

        BoMatrikkeladresse boGateadresse = mapperFacade.map(MatrikkeladresseDTO.builder().kommunenummer(KOMMUNENUMMER).build(), BoMatrikkeladresse.class);

        assertThat(boGateadresse.isGateadresse(), is(equalTo(false)));
        assertThat(boGateadresse.isMatrikkeladresse(), is(equalTo(true)));
        assertThat(boGateadresse.getKommunenr(), is(equalTo(KOMMUNENUMMER)));
    }
}