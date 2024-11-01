package no.nav.testnav.oppdragservice.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.oppdragservice.config.TestConfig;
import no.nav.testnav.oppdragservice.consumer.OppdragConsumer;
import no.nav.testnav.oppdragservice.utilty.Oppdragsdata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ContextConfiguration(classes = TestConfig.class)
class OppdragServiceTest {

    @MockBean
    private OppdragConsumer oppdragConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private OppdragService oppdragService;

    @Test
    void execService_OK() {

        var request = Oppdragsdata.buildOppdragRequest();
        var test = oppdragService.sendInnOppdrag(request);

        System.out.println(test);
    }
 }