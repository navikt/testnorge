package no.nav.testnav.oppdragservice.mapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class OppdragRequestMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new OppdragRequestMappingStrategy());
    }

    @Test
    void mapOrdreOK() {

//        mapperFacade.map()
    }

    private static OppdragRequest buildOppdragRequest() {

        return OppdragRequest.builder()
        .bilagstype(List.of(OppdragRequest.Bilagstype.builder().typeBilag("12").build()))
//        .avstemmingsnokkel()
//        .ompostering()
//        .oppdragslinje()
//        .kodeEndring()
//        .kodeStatus()
//        .datoStatusFom()
//        .kodeFagomraade()
//        .fagsystemId()
//        .oppdragsId()
//        .utbetFrekvens()
//        .datoForfall()
//        .stonadId()
//        .oppdragGjelderId()
//        .datoOppdragGjelderFom()
//        .saksbehId()
//        .enhet()
//        .belopsgrense()
//        .tekst()
        .build();
    }
}