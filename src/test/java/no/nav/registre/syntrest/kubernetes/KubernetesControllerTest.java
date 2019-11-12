package no.nav.registre.syntrest.kubernetes;

import com.google.gson.internal.LinkedTreeMap;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.models.V1DeleteOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@ActiveProfiles("KubernetesTest")
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8082)
@ContextConfiguration(classes = KubernetesTestConfig.class)
@EnableAutoConfiguration
public class KubernetesControllerTest {

    @Autowired
    private CustomObjectsApi customObjectsApi;
    @Autowired
    private RestTemplate restTemplate;

    private KubernetesController kubernetesController;
    @Value("${isAlive}") String isAliveUrl;
    @Value("${docker-image-path}") String dockerImagePath;
    @Value("${max-alive-retries}") int maxRetries;
    @Value("${alive-retry-delay}") int retryDelay;

    private String GROUP = "nais.io",
            VERSION = "v1alpha1",
            NAMESPACE = "q2",
            PLURAL = "applications";
    private V1DeleteOptions deleteOptions;
    private LinkedTreeMap applicationsOnCluster;
    private UriTemplate isAliveUri;

    @Before
    public void setup() throws ApiException {
        kubernetesController = new KubernetesController(customObjectsApi, isAliveUrl, dockerImagePath, maxRetries, retryDelay);
        applicationsOnCluster = new LinkedTreeMap<String, List<LinkedTreeMap<String, String>>>();
        isAliveUri = new UriTemplate(isAliveUrl);

        List<LinkedTreeMap<String, LinkedTreeMap<String, String>>> items = new ArrayList<>();
        LinkedTreeMap<String, String>
                frikortTree = new LinkedTreeMap<>(),
                eiaTree = new LinkedTreeMap<>(),
                inntektTree = new LinkedTreeMap<>(),
                meldekortTree = new LinkedTreeMap<>();
        frikortTree.put("name", "synthdata-frikort");
        eiaTree.put("name", "synthdata-eia");
        inntektTree.put("name", "synthdata-inntekt");
        meldekortTree.put("name", "synthdata-meldekort");
        LinkedTreeMap<String, LinkedTreeMap<String, String>>
                frikortMeta = new LinkedTreeMap<>(),
                eiaMeta = new LinkedTreeMap<>(),
                inntektMeta = new LinkedTreeMap<>(),
                meldekortMeta = new LinkedTreeMap<>();
        frikortMeta.put("metadata", frikortTree);
        eiaMeta.put("metadata", eiaTree);
        inntektMeta.put("metadata", inntektTree);
        meldekortMeta.put("metadata", meldekortTree);
        items.add(frikortMeta);
        items.add(eiaMeta);
        items.add(inntektMeta);
        items.add(meldekortMeta);
        applicationsOnCluster.put("items", items);
        deleteOptions = new V1DeleteOptions();
        Mockito.when(customObjectsApi.listNamespacedCustomObject(
                eq(GROUP), eq(VERSION), eq(NAMESPACE), eq(PLURAL),
                eq(null), eq(null), eq(null), eq(null)))
                .thenReturn(applicationsOnCluster);
        /*Mockito.doNothing().when(
                customObjectsApi.createNamespacedCustomObject(
                        eq(GROUP), eq(VERSION), eq(NAMESPACE), eq(PLURAL),
                        Mockito.any(), eq(null)));*/
        /*Mockito.doNothing().when(
                customObjectsApi.deleteNamespacedCustomObject(
                        eq(GROUP), eq(VERSION), eq(NAMESPACE), eq(PLURAL), Mockito.anyString(), eq(deleteOptions),
                        eq(null), eq(null), eq(null)));*/
    }

    @Test
    public void existingOnCluster() throws ApiException {
        assertTrue(kubernetesController.existsOnCluster("synthdata-frikort"));
        assertTrue(kubernetesController.existsOnCluster("synthdata-eia"));
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


    @Test
    public void deployedButNotUp() throws InterruptedException, ApiException {
        /*stubFor(get(urlEqualTo("/synthdata-frikort/internal/isAlive"))
                .willReturn(ok("1")));*/
        stubFor(get(urlEqualTo("/synthdata-frikort/internal/isAlive"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("404 Not Found")));

        kubernetesController.deployImage("synthdata-frikort");
        Mockito.verify(customObjectsApi, times(0))
                .createNamespacedCustomObject(eq(GROUP), eq(VERSION), eq(NAMESPACE), eq(PLURAL), Mockito.any(), eq(null));
    }

    @Test
    public void normalDeploy() throws InterruptedException, ApiException {
//         stubFor(get(urlEqualTo("/MYAPP/internal/isAlive"))
//                .willReturn(ok("1")));
//         kubernetesController.deployImage("MYAPP");
//         Mockito.verify(customObjectsApi, times(1))
//                 .createNamespacedCustomObject(eq(GROUP), eq(VERSION), eq(NAMESPACE), eq(PLURAL), Mockito.any(), eq(null));
    }

    @Test
    public void shutdownTest() {
        // Mockito.when(kubernetesController.getApi().listNamespacedCustomObject(eq(GROUP), eq(VERSION), eq(NAMESPACE), eq(PLURAL),
        //        eq(null), eq(null), eq(null), eq(null))).thenReturn(applicationsOnCluster);
        /*Mockito.when(kubernetesController.getApi().listNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL,
                null, null, null, null)).thenReturn(applicationsOnCluster);*/
        /*Mockito.when(kubernetesController.getApi().deleteNamespacedCustomObject(eq(GROUP), eq(VERSION), eq(NAMESPACE), eq(PLURAL), anyString(),
                eq(deleteOptions), eq(null), eq(null), eq(null))).thenReturn(1);*/
        /*Mockito.when(kubernetesController.getApi().deleteNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, anyString(),
                deleteOptions, null, null, null)).thenReturn(1);

        kubernetesController.takedownImage("testdata-frikort");

        verify(kubernetesController.getApi()).deleteNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, "testdata-frikort",
                deleteOptions, null, null, null);*/
    }

}
