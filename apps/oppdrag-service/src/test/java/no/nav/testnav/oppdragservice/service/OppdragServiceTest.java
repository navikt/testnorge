package no.nav.testnav.oppdragservice.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.oppdragservice.consumer.OppdragWSConsumer;
import no.nav.testnav.oppdragservice.mapper.LocalDateCustomMapping;
import no.nav.testnav.oppdragservice.mapper.LocalDateTimeCustomMapping;
import no.nav.testnav.oppdragservice.mapper.MapperTestUtils;
import no.nav.testnav.oppdragservice.mapper.OppdragRequestMappingStrategy;
import no.nav.testnav.oppdragservice.mapper.OppdragResponseMappingStrategy;
import no.nav.testnav.oppdragservice.utilty.Oppdragsdata;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.TEXT_VALUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OppdragServiceTest {

    private static final String MILJOE = "q2";

    @Mock
    private OppdragWSConsumer oppdragWSConsumer;

    @Spy
    private MapperFacade mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(
            List.of(new LocalDateCustomMapping(), new LocalDateTimeCustomMapping()),
            new OppdragRequestMappingStrategy(), new OppdragResponseMappingStrategy());

    @InjectMocks
    private OppdragService oppdragService;

    @Test
    void execOppdragServiceNominal_OK() {

        when(oppdragWSConsumer.sendOppdrag(anyString(), any(SendInnOppdragRequest.class)))
                .thenReturn(Oppdragsdata.buildOppdragResponse());

        var request = Oppdragsdata.buildOppdragRequest();
        var target = oppdragService.sendInnOppdrag(MILJOE, request);

        assertThat(target.getInfomelding().getBeskrMelding(), is(equalTo(TEXT_VALUE)));
    }

    @Test
    void execOppdragServiceOppdragslinje_OK() {

        when(oppdragWSConsumer.sendOppdrag(anyString(), any(SendInnOppdragRequest.class)))
                .thenReturn(Oppdragsdata.buildOppdragslinjeResponse());

        var request = Oppdragsdata.buildOppdragsLinjeRequest();
        var target = oppdragService.sendInnOppdrag(MILJOE, request);

        assertThat(target.getInfomelding().getBeskrMelding(), is(equalTo(TEXT_VALUE)));
    }
 }