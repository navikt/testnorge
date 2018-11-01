package no.nav.dolly.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import no.nav.dolly.exceptions.JiraException;
import no.nav.dolly.jira.JiraConsumer;
import no.nav.dolly.jira.domain.AllowedValue;
import no.nav.dolly.jira.domain.Field;
import no.nav.dolly.jira.domain.Fields;
import no.nav.dolly.jira.domain.Issuetypes;
import no.nav.dolly.jira.domain.Project;

@RunWith(MockitoJUnitRunner.class)
public class OpenAmServiceTest {

    private static final String IDENT1 = "11111111111";
    private static final String IDENT2 = "22222222222";
    private static final String IDENT3 = "33333333333";
    private static final String MILJOE1 = "t0";

    private static final String FEILMELDING1 = "En feil oppsto. Bestilling kan ikke utføres.";
    private static final String FEILMELDING2 = "Angitt miljø eksisterer ikke.";

    @Mock
    private JiraConsumer jiraConsumer;

    @InjectMocks
    private OpenAmService openAmService;

    @Mock
    private ResponseEntity<Project> projectHttpEntity;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        when(jiraConsumer.excuteRequest(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Project.class))).thenReturn(projectHttpEntity);
        when(projectHttpEntity.getBody()).thenReturn(Project.builder()
                .projects(Arrays.asList(Project.builder()
                        .issuetypes(Arrays.asList(Issuetypes.builder()
                                .fields(Fields.builder().build())
                                .build()))
                        .build()))
                .build());
    }

    @Test
    public void opprettIdenterMiljoeNotFound() {
        when(projectHttpEntity.getBody()).thenReturn(Project.builder()
                .projects(Arrays.asList(Project.builder()
                        .issuetypes(Arrays.asList(Issuetypes.builder()
                                .fields(Fields.builder()
                                        .customfield_14811(Field.builder().build())
                                        .project(Field.builder().build())
                                        .issuetype(Field.builder().build())
                                        .build())
                                .build()))
                        .build()))
                .build());
        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(Arrays.asList(IDENT1, IDENT2, IDENT3), MILJOE1);
        verify(jiraConsumer).excuteRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any());
        assertThat(openAmResponse.getFeilmelding(), is(equalTo(FEILMELDING2)));
        assertThat(openAmResponse.getHttpCode(), is(equalTo(500)));
        assertThat(openAmResponse.getStatus(), is("FEIL"));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }

    @Test
    public void opprettIdenterOK() {
        when(projectHttpEntity.getBody()).thenReturn(Project.builder()
                .projects(Arrays.asList(Project.builder()
                        .issuetypes(Arrays.asList(Issuetypes.builder()
                                .fields(Fields.builder()
                                        .customfield_14811(Field.builder()
                                                .allowedValues(Arrays.asList(AllowedValue.builder()
                                                        .value(MILJOE1)
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .project(Field.builder().build())
                                        .issuetype(Field.builder().build())
                                        .build())
                                .build()))
                        .build()))
                .build());
        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(Arrays.asList(IDENT1, IDENT2, IDENT3), MILJOE1);
        verify(jiraConsumer, times(3)).excuteRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any());
        assertThat(openAmResponse.getFeilmelding(), is(equalTo(FEILMELDING2)));
        assertThat(openAmResponse.getHttpCode(), is(equalTo(500)));
        assertThat(openAmResponse.getStatus(), is("FEIL"));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }

    @Test
    public void opprettIdenterFeiler() {

        expectedException.expect(JiraException.class);
        expectedException.expectMessage(FEILMELDING1);
        openAmService.opprettIdenter(Arrays.asList(IDENT1, IDENT2, IDENT3), MILJOE1);
    }

}