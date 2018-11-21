package no.nav.identpool.navnepool.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import no.nav.identpool.navnepool.NavnepoolService;

@RunWith(MockitoJUnitRunner.class)
public class GeneratorControllerTest {

    @Mock
    private NavnepoolService service;

    @InjectMocks
    private GeneratorController generatorController;

    /**
     * Tester at service blir kalt med antall navn som input NÅR REST-endepunktet blir kalt.
     * Antall har default verdi 1. Dvs. dersom antall er null/ikke oppgitt, så omgjøres antall til 1.
     *
     * MockMvc og Mockito blir brukt.
     */
    @Test
    public void shouldCallServiceWithAmount() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(generatorController).build();
        mockMvc.perform(get("/api/v1/navnepool/tilfeldig"));

        verify(service).finnTilfeldigeNavn(1);
    }

}