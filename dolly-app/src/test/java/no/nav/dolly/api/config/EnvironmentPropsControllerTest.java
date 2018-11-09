package no.nav.dolly.api.config;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.domain.resultset.tpsf.RsDollyProps;
import no.nav.dolly.properties.ProvidersProps;

@RunWith(MockitoJUnitRunner.class)
public class EnvironmentPropsControllerTest {

    private static final String TPSF = "tpsf";
    private static final String SIGRUNSTUB = "sigrunstub";
    private static final String KRRSTUB = "krrstub";
    private static final String KODEVERK = "kodeverk";

    @Mock ProvidersProps providersProps;

    @InjectMocks EnvironmentPropsController environmentPropsController;

    @Before
    public void setup(){
        ProvidersProps.Tpsf tpsf = new ProvidersProps.Tpsf();
        tpsf.setUrl(TPSF);
        ProvidersProps.KrrStub krrStub = new ProvidersProps.KrrStub();
        krrStub.setUrl(KRRSTUB);
        ProvidersProps.SigrunStub sigrunStub = new ProvidersProps.SigrunStub();
        sigrunStub.setUrl(SIGRUNSTUB);
        ProvidersProps.Kodeverk kodeverk = new ProvidersProps.Kodeverk();
        kodeverk.setUrl(KODEVERK);
        when(providersProps.getTpsf()).thenReturn(tpsf);
        when(providersProps.getKodeverk()).thenReturn(kodeverk);
        when(providersProps.getSigrunStub()).thenReturn(sigrunStub);
        when(providersProps.getKrrStub()).thenReturn(krrStub);
    }

    @Test
    public void getEnvironmentProps() {
        RsDollyProps props = environmentPropsController.getEnvironmentProps();
        assertThat(props.getKodeverkUrl(), is(KODEVERK));
        assertThat(props.getKrrStubUrl(), is(KRRSTUB));
        assertThat(props.getTpsfUrl(), is(TPSF));
        assertThat(props.getSigrunStubUrl(), is(SIGRUNSTUB));
    }
}