package no.nav.registre.syntrest.kubernetes;

import com.google.gson.internal.LinkedTreeMap;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.models.V1DeleteOptions;
import no.nav.registre.syntrest.config.AppConfig;
import no.nav.registre.syntrest.config.KubernetesConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.temporaryRedirect;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

// import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static wiremock.com.github.jknack.handlebars.helper.ConditionalHelpers.eq;


@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8082)
@ContextConfiguration(classes = {KubernetesController.class, AppConfig.class, KubernetesConfig.class})
@EnableAutoConfiguration
public class KubernetesControllerTest {

    @MockBean
    ApiClient apiClient;

    @Autowired
    RestTemplate restTemplate;

    @Mock
    CustomObjectsApi customObjectsApi;
    /*
    api.listNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, null, null, null, null)
    api.deleteNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, appName, deleteOptions, null, null, null)
    api.createNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, manifestFile, null)
     */

    private KubernetesController kubernetesController;
    @Value("${isAlive}") String isAliveUrl;
    @Value("${docker-image-path}") String dockerImagePath;
    @Value("${max-alive-retries}") int maxRetries;
    @Value("${alive-retry-delay}") int retryDelay;

    private String applicationsOnCluster;
    private String GROUP = "nais.io",
            VERSION = "v1alpha1",
            NAMESPACE = "q2",
            PLURAL = "applications";
    private V1DeleteOptions deleteOptions;

    @Before
    public void setUp() {
        initMocks(this);
        kubernetesController = new KubernetesController(restTemplate, apiClient, isAliveUrl, dockerImagePath, maxRetries, retryDelay);

        applicationsOnCluster = "{\"items\":[" +
                "{\"metadata\":{\"name\":\"testdata-frikort\"}}," +
                "{\"metadata\":{\"name\":\"testdata-eia\"}}," +
                "{\"metadata\":{\"name\":\"testdata-inntekt\"}}," +
                "{\"metadata\":{\"name\":\"testdata-meldekort\"}}" +
                "]}";
        /*applicationsOnCluster = new LinkedTreeMap<>();
        LinkedTreeMap<String, >
        List<String> tmp = new ArrayList<>();
        tmp.add("testdata-frikort");
        tmp.add("testdata-inntekt");
        tmp.add("testdata-meldekort");
        tmp.add("testdata-eia");

        applicationsOnCluster.put("items", tmp);*/

        deleteOptions = new V1DeleteOptions();
    }


    // Sjekk return hvis det tar for lang tid å deploye
    @Test
    public void deploymentTime() {

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

}
