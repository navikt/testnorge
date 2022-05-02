package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoMatrikkeladresse;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

class PdlPersonStrategyMapperTest {

    private static final String ADRESSENAVN = "Gategaten";
    private static final String HUSNUMMER = "123";
    private static final String ADRESSEKODE = "789";
    private static final String KOMMUNENUMMER = "1234";

    private MapperFacade mapperFacade;

    @BeforeEach
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(), new PdlPersonStrategyMapper());
    }

    @Test
    public void vegadresseMapping_OK() {

        BoGateadresse boGateadresse = (BoGateadresse) mapperFacade.map(VegadresseDTO.builder()
                        .adressenavn(ADRESSENAVN)
                        .kommunenummer(KOMMUNENUMMER)
                        .husnummer(HUSNUMMER)
                        .adressekode(ADRESSEKODE)
                        .build(),
                BoAdresse.class);

        assertThat(boGateadresse.isGateadresse(), is(equalTo(true)));
        assertThat(boGateadresse.isMatrikkeladresse(), is(equalTo(false)));
        assertThat(boGateadresse.getGatekode(), is(equalTo(ADRESSEKODE)));
        assertThat(boGateadresse.getGateadresse(), is(equalTo(ADRESSENAVN)));
        assertThat(boGateadresse.getHusnummer(), is(equalTo(HUSNUMMER)));
        assertThat(boGateadresse.getKommunenr(), is(equalTo(KOMMUNENUMMER)));
    }

    @Test
    public void matrikkeladresseMapping_OK() {

        BoMatrikkeladresse boGateadresse = (BoMatrikkeladresse) mapperFacade.map(MatrikkeladresseDTO.builder()
                        .tilleggsnavn(ADRESSENAVN)
                        .kommunenummer(KOMMUNENUMMER)
                        .gaardsnummer(Integer.parseInt(HUSNUMMER))
                        .bruksnummer(Integer.parseInt(ADRESSEKODE))
                        .build(),
                BoAdresse.class);

        assertThat(boGateadresse.isGateadresse(), is(equalTo(false)));
        assertThat(boGateadresse.isMatrikkeladresse(), is(equalTo(true)));
        assertThat(boGateadresse.getKommunenr(), is(equalTo(KOMMUNENUMMER)));
        assertThat(boGateadresse.getTilleggsadresse(), is(equalTo(ADRESSENAVN)));
        assertThat(boGateadresse.getBruksnr(), is(equalTo(ADRESSEKODE)));
        assertThat(boGateadresse.getGardsnr(), is(equalTo(HUSNUMMER)));
    }

    @Test
    public void BoAdresseMappingBeggeTyper_OK() {

        var person = Person.builder()
                .boadresse(Stream.of(
                                mapperFacade.mapAsList(List.of(VegadresseDTO.builder().build()),
                                        BoAdresse.class),
                                mapperFacade.mapAsList(List.of(MatrikkeladresseDTO.builder().build()),
                                        BoAdresse.class))
                        .flatMap(Collection::stream)
                        .toList())
                .build();

        var boAdresser = person.getBoadresse();

        assertThat(boAdresser.stream().filter(BoAdresse::isGateadresse).toList(), hasSize(1));
        assertThat(boAdresser.stream().filter(BoAdresse::isMatrikkeladresse).toList(), hasSize(1));
    }
}