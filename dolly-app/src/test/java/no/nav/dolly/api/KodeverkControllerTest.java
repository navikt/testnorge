package no.nav.dolly.api;

import no.nav.dolly.domain.kodeverk.Kodeverk;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class KodeverkControllerTest {

    @InjectMocks
    private KodeverkController controller;

    @Test
    public void fetchKodeverk(){
        Kodeverk kodeverk = controller.fetchKodeverk();
        assertThat(kodeverk, is(notNullValue()));
    }
}