package no.nav.dolly.consumer.jira;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.dolly.consumer.jira.JiraConsumer;
import no.nav.dolly.domain.jira.Project;
import no.nav.dolly.properties.JiraProps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class JiraConsumerTest {

    private static final String HOST = "host";
    private static final String USER = "user";
    private static final String PWD = "pwd";
    private static final String AUX_HEADER = "header";
    private static final String AUX_HEADER_CONTENTS = "aux";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JiraProps jiraProps;

    @InjectMocks
    private JiraConsumer jiraConsumer;

    @Mock
    private HttpEntity httpEntity;

    @Before
    public void setup() {
        when(jiraProps.getHost()).thenReturn(HOST);
        when(jiraProps.getUsername()).thenReturn(USER);
        when(jiraProps.getPassword()).thenReturn(PWD);
    }

    @Test
    public void excuteRequest() {
        jiraConsumer.excuteRequest("url", HttpMethod.GET, httpEntity, Project.class);

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), eq(httpEntity), eq(Project.class));
    }

    @Test
    public void createHttpHeaders() {
        HttpHeaders httpHeaders = jiraConsumer.createHttpHeaders(MediaType.APPLICATION_JSON);

        assertThat(httpHeaders.size(), is(equalTo(3)));
        assertThat(httpHeaders.get("X-Atlassian-Token"), containsInAnyOrder("no-check"));
        assertThat(httpHeaders.get(HttpHeaders.CONTENT_TYPE), containsInAnyOrder(MediaType.APPLICATION_JSON_VALUE));
        assertThat(httpHeaders.get(HttpHeaders.AUTHORIZATION), containsInAnyOrder("Basic dXNlcjpwd2Q="));
    }

    @Test
    public void createHttpHeadersExtraHeader() {
        HttpHeaders inputHeaders = new HttpHeaders();
        inputHeaders.add(AUX_HEADER, AUX_HEADER_CONTENTS);
        HttpHeaders httpHeaders = jiraConsumer.createHttpHeaders(MediaType.MULTIPART_FORM_DATA, inputHeaders);

        assertThat(httpHeaders.get(HttpHeaders.CONTENT_TYPE), containsInAnyOrder(MediaType.MULTIPART_FORM_DATA_VALUE));
        assertThat(httpHeaders.get(AUX_HEADER), containsInAnyOrder(AUX_HEADER_CONTENTS));
    }

    @Test
    public void getBaseUrl() {
        String baseUrl = jiraConsumer.getBaseUrl();
        assertThat(baseUrl, is(equalTo(HOST)));
    }
}