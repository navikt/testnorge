package no.nav.dolly.service;

import no.nav.dolly.consumer.jira.JiraConsumer;
import no.nav.dolly.domain.jira.AllowedValue;
import no.nav.dolly.domain.jira.Field;
import no.nav.dolly.domain.jira.Fields;
import no.nav.dolly.domain.jira.Issuetypes;
import no.nav.dolly.domain.jira.JiraResponse;
import no.nav.dolly.domain.jira.Project;
import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OpenAmServiceTest {

    private static final String IDENT1 = "11111111111";
    private static final String IDENT2 = "22222222222";
    private static final String MILJOE1 = "t0";
    private static final String MILJOE2 = "t1";

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

    @Mock
    private HttpServerErrorException httpServerErrorException;

    @BeforeEach
    public void setup() {
        when(jiraConsumer.excuteRequest(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Project.class))).thenReturn(projectResponseEntity);
        when(jiraConsumer.getOpenAmMetadata(anyString(), any(HttpEntity.class), any())).thenReturn(projectResponseEntity);
        when(jiraConsumer.excuteRequest(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(JiraResponse.class))).thenReturn(jiraResponseEntity);
        when(jiraResponseEntity.getBody()).thenReturn(JiraResponse.builder().build());
        when(jiraConsumer.excuteRequest(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenReturn(stringResponseEntity);
        when(stringResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
    }

    @Test
    public void opprettIdenterOK() {
        when(projectResponseEntity.getBody()).thenReturn(Project.builder()
                .projects(singletonList(Project.builder()
                        .issuetypes(singletonList(Issuetypes.builder()
                                .fields(Fields.builder()
                                        .customfield_14811(Field.builder()
                                                .allowedValues(singletonList(AllowedValue.builder()
                                                        .value(MILJOE1)
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .project(Field.builder()
                                                .allowedValues(singletonList(AllowedValue.builder()
                                                        .value("project1")
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .issuetype(Field.builder()
                                                .allowedValues(singletonList(AllowedValue.builder()
                                                        .value("issuetype1")
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .build())
                                .build()))
                        .build()))
                .build());

        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(asList(IDENT1, IDENT2), MILJOE1);

        verify(jiraConsumer, times(2)).excuteRequest(anyString(), any(HttpMethod.class), any(HttpEntity.class), any());
        verify(jiraConsumer, times(1)).getOpenAmMetadata(anyString(), any(HttpEntity.class), any());
        assertThat(openAmResponse.getHttpCode(), is(equalTo(HttpStatus.OK.value())));
        assertThat(openAmResponse.getStatus(), is(HttpStatus.OK));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }

    @Test
    public void opprettIdenterFeilerIngenMetadata() {
        when(projectResponseEntity.getBody()).thenReturn(null);

        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(asList(IDENT1, IDENT2), MILJOE1);

        verify(jiraConsumer).getOpenAmMetadata(anyString(), any(HttpEntity.class), any());
        assertThat(openAmResponse.getMessage(), is(equalTo(FEILMELDING1)));
        assertThat(openAmResponse.getHttpCode(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        assertThat(openAmResponse.getStatus(), is(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }

    @Test
    public void opprettIdenterFeilerUfullstendigMetadata() {
        when(projectResponseEntity.getBody()).thenReturn(Project.builder()
                .projects(singletonList(Project.builder()
                        .issuetypes(singletonList(Issuetypes.builder()
                                .fields(Fields.builder().build())
                                .build()))
                        .build()))
                .build());

        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(asList(IDENT1, IDENT2), MILJOE1);

        verify(jiraConsumer).getOpenAmMetadata(anyString(), any(HttpEntity.class), any());
        assertThat(openAmResponse.getMessage(), is(equalTo(FEILMELDING1)));
        assertThat(openAmResponse.getHttpCode(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        assertThat(openAmResponse.getStatus(), is(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }

    @Test
    public void opprettIdenterMetadataHarTommeFelter() {
        when(projectResponseEntity.getBody()).thenReturn(Project.builder()
                .projects(singletonList(Project.builder()
                        .issuetypes(singletonList(Issuetypes.builder()
                                .fields(Fields.builder()
                                        .customfield_14811(Field.builder().build())
                                        .project(Field.builder().build())
                                        .issuetype(Field.builder().build())
                                        .build())
                                .build()))
                        .build()))
                .build());

        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(asList(IDENT1, IDENT2), MILJOE1);

        verify(jiraConsumer).getOpenAmMetadata(anyString(), any(HttpEntity.class), any());
        assertThat(openAmResponse.getMessage(), is(equalTo(FEILMELDING1)));
        assertThat(openAmResponse.getHttpCode(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        assertThat(openAmResponse.getStatus(), is(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }

    @Test
    public void opprettIdenterUgyldigMiljoe() {
        when(projectResponseEntity.getBody()).thenReturn(Project.builder()
                .projects(singletonList(Project.builder()
                        .issuetypes(singletonList(Issuetypes.builder()
                                .fields(Fields.builder()
                                        .customfield_14811(Field.builder()
                                                .allowedValues(singletonList(AllowedValue.builder()
                                                        .value(MILJOE2)
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .project(Field.builder()
                                                .allowedValues(singletonList(AllowedValue.builder()
                                                        .value("project1")
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .issuetype(Field.builder()
                                                .allowedValues(singletonList(AllowedValue.builder()
                                                        .value("issuetype1")
                                                        .id("1")
                                                        .build()))
                                                .build())
                                        .build())
                                .build()))
                        .build()))
                .build());

        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(asList(IDENT1, IDENT2), MILJOE1);

        verify(jiraConsumer).getOpenAmMetadata(anyString(), any(HttpEntity.class), any());
        assertThat(openAmResponse.getMessage(), is(equalTo(FEILMELDING2)));
        assertThat(openAmResponse.getHttpCode(), is(equalTo(HttpStatus.BAD_REQUEST.value())));
        assertThat(openAmResponse.getStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }

    @Test
    public void opprettIdentThrowsHttpException() {

        when(jiraConsumer.getOpenAmMetadata(anyString(), any(HttpEntity.class), any())).thenThrow(httpServerErrorException);
        when(httpServerErrorException.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);

        RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(asList(IDENT1, IDENT2), MILJOE1);

        verify(jiraConsumer).getOpenAmMetadata(anyString(), any(HttpEntity.class), any());
        assertThat(openAmResponse.getHttpCode(), is(equalTo(HttpStatus.UNAUTHORIZED.value())));
        assertThat(openAmResponse.getStatus(), is(HttpStatus.UNAUTHORIZED));
        assertThat(openAmResponse.getMiljoe(), is(equalTo(MILJOE1)));
    }
}