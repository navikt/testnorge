package no.nav.registre.testnorge.sykemelding.mapper;

import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SykemeldingValidateMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {

        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new SykemeldingValidateMappingStrategy());
    }

    @Test
    void validate() {

        // TBD
    }
}