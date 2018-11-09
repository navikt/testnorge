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

import no.nav.dolly.domain.resultset.tpsf.RsTpsfProps;
import no.nav.dolly.properties.ProvidersProps;

@RunWith(MockitoJUnitRunner.class)
public class EnvironmentPropsControllerTest {

    @Mock ProvidersProps providersProps;
    @InjectMocks EnvironmentPropsController environmentPropsController;

    @Before
    public void setup(){
        ProvidersProps.Tpsf tpsf = new ProvidersProps.Tpsf();
        tpsf.setUrl("url");
        when(providersProps.getTpsf()).thenReturn(tpsf);
    }

    @Test
    public void getEnvironmentProps() {
        RsTpsfProps props = environmentPropsController.getEnvironmentProps();
        assertThat(props.getUrl(), is("url"));
    }
}