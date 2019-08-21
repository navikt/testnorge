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
    private static final String ARENAFORVALTER = "arenaforvalter";
    private static final String INSTDATA = "instdata";

    @Mock
    private ProvidersProps providersProps;

    @InjectMocks
    private EnvironmentPropsController environmentPropsController;

    @Before
    public void setup() {
        when(providersProps.getTpsf()).thenReturn(ProvidersProps.Tpsf.builder()
                .url(TPSF).build());
        when(providersProps.getKodeverk()).thenReturn(ProvidersProps.Kodeverk.builder()
                .url(KODEVERK).build());
        when(providersProps.getSigrunStub()).thenReturn(ProvidersProps.SigrunStub.builder()
                .url(SIGRUNSTUB).build());
        when(providersProps.getKrrStub()).thenReturn(ProvidersProps.KrrStub.builder()
                .url(KRRSTUB).build());
        when(providersProps.getArenaForvalter()).thenReturn(ProvidersProps.ArenaForvalter.builder()
                .url(ARENAFORVALTER).build());
        when(providersProps.getInstdata()).thenReturn(ProvidersProps.Instdata.builder()
                .url(INSTDATA).build());
    }

    @Test
    public void getEnvironmentProps() {
        RsDollyProps props = environmentPropsController.getEnvironmentProps();
        assertThat(props.getKodeverkUrl(), is(KODEVERK));
        assertThat(props.getKrrStubUrl(), is(KRRSTUB));
        assertThat(props.getTpsfUrl(), is(TPSF));
        assertThat(props.getSigrunStubUrl(), is(SIGRUNSTUB));
        assertThat(props.getArenaForvalterUrl(), is(ARENAFORVALTER));
        assertThat(props.getInstdataUrl(), is(INSTDATA));
    }
}