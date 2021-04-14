package no.nav.registre.syntrest.kubernetes;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.internal.LinkedTreeMap;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CustomObjectsApi;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles({"test", "kubernetesTest"})
public class KubernetesControllerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
            wireMockConfig().httpsPort(8088).port(8089));

    @Autowired
    private CustomObjectsApi customObjectsApi;

    @Autowired
    private KubernetesController kubernetesController;
    @Value("${isAlive}") String isAliveUrl;

    private String GROUP = "nais.io",
            VERSION = "v1alpha1",
            NAMESPACE = "dolly",
            PLURAL = "applications";

    @Before
    public void setup() throws ApiException {
        LinkedTreeMap applicationsOnCluster = new LinkedTreeMap<String, List<LinkedTreeMap<String, String>>>();

        List<LinkedTreeMap<String, LinkedTreeMap<String, String>>> items = new ArrayList<>();
        LinkedTreeMap<String, String>
                frikortTree = new LinkedTreeMap<>(),
                inntektTree = new LinkedTreeMap<>(),
                meldekortTree = new LinkedTreeMap<>();
        frikortTree.put("name", "synthdata-frikort");
        inntektTree.put("name", "synthdata-inntekt");
        meldekortTree.put("name", "synthdata-meldekort");
        LinkedTreeMap<String, LinkedTreeMap<String, String>>
                frikortMeta = new LinkedTreeMap<>(),
                inntektMeta = new LinkedTreeMap<>(),
                meldekortMeta = new LinkedTreeMap<>();
        frikortMeta.put("metadata", frikortTree);
        inntektMeta.put("metadata", inntektTree);
        meldekortMeta.put("metadata", meldekortTree);
        items.add(frikortMeta);
        items.add(inntektMeta);
        items.add(meldekortMeta);
        applicationsOnCluster.put("items", items);
        Mockito.when(customObjectsApi.listNamespacedCustomObject(
                eq(GROUP), eq(VERSION), eq(NAMESPACE), eq(PLURAL),
                eq(null), eq(null), eq(null), eq(null)))
                .thenReturn(applicationsOnCluster);
    }

    @Test
    public void existingOnCluster() throws ApiException {
        assertTrue(kubernetesController.existsOnCluster("synthdata-frikort"));
        assertTrue(kubernetesController.existsOnCluster("synthdata-inntekt"));
        assertTrue(kubernetesController.existsOnCluster("synthdata-meldekort"));
        assertFalse(kubernetesController.existsOnCluster("NOAPP"));
    }

    @Test
    public void isAliveTest() {
        stubFor(get(urlEqualTo("/MYAPP/internal/isAlive"))
                .willReturn(ok("1")));
        assertThat(kubernetesController.isAlive("MYAPP"), is(true));
    }

    @Test
    public void isNotAliveTest() {
        stubFor(get(urlEqualTo("/MYAPP/internal/isAlive"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("404 Not Found")));
        assertThat(kubernetesController.isAlive("MYAPP"), is(false));
    }


    @Test(expected = RuntimeException.class)
    public void deployedButNotUp() throws InterruptedException, ApiException {
        githubTagStub();
        stubFor(get(urlEqualTo("/synthdata-frikort/internal/isAlive"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("404 Not Found")));
        stubFor(delete(urlEqualTo("/dummyDelete/app/" + NAMESPACE + "/synthdata-frikort"))
                .willReturn(ok()));

        kubernetesController.deployImage("synthdata-frikort");
        Mockito.verify(customObjectsApi, times(0))
                .createNamespacedCustomObject(eq(GROUP), eq(VERSION), eq(NAMESPACE), eq(PLURAL), Mockito.any(), eq(null));
    }

    private void githubTagStub() {
        stubFor(post("/graphql")
                .withBasicAuth("dummy", "dummy")
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("{\"version\": \"myTag\"}")));
    }

}
