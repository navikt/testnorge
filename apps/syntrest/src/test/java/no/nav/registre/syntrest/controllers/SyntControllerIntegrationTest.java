package no.nav.registre.syntrest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CustomObjectsApi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static java.util.Objects.isNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class SyntControllerIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
            wireMockConfig().httpsPort(8088).port(8089));

    @MockBean(name = "customObjectsApi")
    private CustomObjectsApi api;

    @Autowired ApplicationContext applicationContext;
    @Autowired SyntController syntController;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void applicationContextTest() {
        assertThat(Arrays.asList(applicationContext.getBeanDefinitionNames())
                .containsAll(Arrays.asList("customObjectsApi", "kubernetesController", "naisYaml", "applicationManager")), is(true));
    }

    @Test
    public void addQueryParamTest() throws Exception {
        launchApplicationStubs("synthdata-arena-meldekort");
        mockApiMethods();
        stubFor(get(urlEqualTo("/generate_meldekort/2/ATTF?arbeidstimer=3.0"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"Meldekort_XML_1\", \"Meldekort_XML_2\"]")));
        var result = syntController.generateMeldekort("ATTF", 2, 3.0);
        assertThat(!isNull(result.getBody()), is(true));
        assertThat(result.getBody().size(), is(2));
    }

    private void launchApplicationStubs(String appName) {
        stubFor(post("/graphql")
                .withBasicAuth("dummy", "dummy")
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("{\"version\": \"myTag\"}")));

        String isAliveUrl = "/" + appName + "/internal/isAlive";
        stubFor(get(isAliveUrl).inScenario("StartApplication")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(200).withBody("0"))
                .willSetStateTo("StartApp"));
        stubFor(get(isAliveUrl).inScenario("StartApplication")
                .whenScenarioStateIs("StartApp")
                .willReturn(aResponse().withStatus(200).withBody("1"))
                .willSetStateTo("StartApp"));
    }

    private void mockApiMethods() throws ApiException, JsonProcessingException {
        String jsonMap = "{\"items\": [" +
                "{\"metadata\": {\"name\": \"synthdata-nav\"}}," +
                "{\"metadata\": {\"name\": \"synthdata-popp\"}}" +
                "]}";
        JsonNode jsonNode = objectMapper.readTree(jsonMap);
        Map<String, Object> clusterMap = objectMapper.convertValue(jsonNode, new TypeReference<>() {
        });

        String emptyJsonMap = "{\"items\": []}";
        JsonNode emptyNode = objectMapper.readTree(emptyJsonMap);
        Map<String, Object> emptyMap = objectMapper.convertValue(emptyNode, new TypeReference<>() {
        });

        Mockito.when(api.listNamespacedCustomObject(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(emptyMap)
                .thenReturn(clusterMap);
    }
}
