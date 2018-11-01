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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import no.nav.dolly.jira.JiraConsumer;
import no.nav.dolly.jira.domain.AllowedValue;
import no.nav.dolly.jira.domain.Field;
import no.nav.dolly.jira.domain.Fields;
import no.nav.dolly.jira.domain.Issuetypes;
import no.nav.dolly.jira.domain.JiraResponse;
import no.nav.dolly.jira.domain.Project;

@RunWith(MockitoJUnitRunner.class)
public class OpenAmServiceTest {

    private static final String IDENT1 = "11111111111";
    private static final String IDENT2 = "22222222222";
    private static final String MILJOE1 = "t0";
    private static final String MILJOE2 = "t1";

    private static final String FEIL = "FEIL";
    private static final String OK = "OK";
    private static final String FEILMELDING1 = "En feil oppsto. Bestilling kan ikke utføres.";
    private static final String FEILMELDING2 = "Angitt miljø eksisterer ikke.";

    @Mock
    private JiraConsumer jiraConsumer;

    @InjectMocks
    private OpenAmService openAmService;

    @Mock
    private ResponseEntity<Project> projectResponseEntity;

    @Mock
    private ResponseEntity<JiraResponse> jiraResponseEntity;

    @Mock
    private ResponseEntity<String> stringResponseEntity;

    @Before
    public void setup() {
        when(jiraConsumer.excuteRequest(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Project.class))).thenReturn(projectResponseEntity);
        when(jiraConsumer.excuteRequest(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(JiraResponse.class))).thenReturn(jiraResponseEntity);
        when(jiraResponseEntity.getBody()).thenReturn(JiraResponse.builder().build());
        when(jiraConsumer.excuteRequest(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenReturn(stringResponseEntity);
        when(stringResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
    }

    @Test
    public void opprettIdenterOK() {
        when(projectResponseEntity.getBody()).thenReturn(Project.builder()
                .projects(Arrays.asList(Project.builder()
                        .issuetypes(Arrays.asList(Issuetypes.builder()
                                .fields(Fields.builder()
                                        .customfield_14811(Field.builder()
                                                .allowedValues(Arrays.asList(AllowedValue.builder()
                                                        .value(MILJOE1)
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .project(Field.builder()
                                                .allowedValues(Arrays.asList(AllowedValue.builder()
                                                        .value("project1")
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .issuetype(Field.builder()
                                                .allowedValues(Arrays.asList(AllowedValue.builder()
                                                        .value("issuetype1")
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .build())
                                .build()))
                        .build()))
                .build());

        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(Arrays.asList(IDENT1, IDENT2), MILJOE1);

        verify(jiraConsumer, times(3)).excuteRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any());
        assertThat(openAmResponse.getHttpCode(), is(equalTo(HttpStatus.OK)));
        assertThat(openAmResponse.getStatus(), is(OK));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }

    @Test
    public void opprettIdenterFeilerUfullstendigMetadata() {
        when(projectResponseEntity.getBody()).thenReturn(Project.builder()
                .projects(Arrays.asList(Project.builder()
                        .issuetypes(Arrays.asList(Issuetypes.builder()
                                .fields(Fields.builder().build())
                                .build()))
                        .build()))
                .build());

        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(Arrays.asList(IDENT1, IDENT2), MILJOE1);

        verify(jiraConsumer).excuteRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any());
        assertThat(openAmResponse.getFeilmelding(), is(equalTo(FEILMELDING1)));
        assertThat(openAmResponse.getHttpCode(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR)));
        assertThat(openAmResponse.getStatus(), is(FEIL));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }

    @Test
    public void opprettIdenterMiljoeMetadataHarTommeFelter() {
        when(projectResponseEntity.getBody()).thenReturn(Project.builder()
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

        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(Arrays.asList(IDENT1, IDENT2), MILJOE1);

        verify(jiraConsumer).excuteRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any());
        assertThat(openAmResponse.getFeilmelding(), is(equalTo(FEILMELDING1)));
        assertThat(openAmResponse.getHttpCode(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR)));
        assertThat(openAmResponse.getStatus(), is(FEIL));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }

    @Test
    public void opprettIdenterMiljoeUgyldigMiljoe() {
        when(projectResponseEntity.getBody()).thenReturn(Project.builder()
                .projects(Arrays.asList(Project.builder()
                        .issuetypes(Arrays.asList(Issuetypes.builder()
                                .fields(Fields.builder()
                                        .customfield_14811(Field.builder()
                                                .allowedValues(Arrays.asList(AllowedValue.builder()
                                                        .value(MILJOE2)
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .project(Field.builder()
                                                .allowedValues(Arrays.asList(AllowedValue.builder()
                                                        .value("project1")
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .issuetype(Field.builder()
                                                .allowedValues(Arrays.asList(AllowedValue.builder()
                                                        .value("issuetype1")
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .build())
                                .build()))
                        .build()))
                .build());

        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(Arrays.asList(IDENT1, IDENT2), MILJOE1);

        verify(jiraConsumer).excuteRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any());
        assertThat(openAmResponse.getFeilmelding(), is(equalTo(FEILMELDING2)));
        assertThat(openAmResponse.getHttpCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(openAmResponse.getStatus(), is(FEIL));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }
}