package no.nav.dolly.api.config;

import no.nav.dolly.domain.resultset.tpsf.RsTpsfProps;
import no.nav.dolly.properties.ProvidersProps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnvironmentPropsControllerTest {

    @Mock ProvidersProps providersProps;
    @InjectMocks EnvironmentPropsController environmentPropsController;

    @Test
    public void getEnvironmentProps() {
        when(providersProps.getTpsf().getUrl()).thenReturn("url");
        RsTpsfProps props = environmentPropsController.getEnvironmentProps();
        assertThat(props.getUrl(), is("url"));
    }
}