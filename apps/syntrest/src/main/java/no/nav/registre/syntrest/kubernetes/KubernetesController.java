package no.nav.registre.syntrest.kubernetes;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.models.V1DeleteOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import org.yaml.snakeyaml.Yaml;

import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KubernetesController {

    private static final int TIMEOUT = 240_000;
    private final String GROUP = "nais.io";
    private final String VERSION = "v1alpha1";
    private final String NAMESPACE = "q2";
    private final String PLURAL = "applications";
    private final String manifestPath;
    private final UriTemplate isAliveUri;
    private final CustomObjectsApi api;
    private final String dockerImagePath;
    private final int maxRetries;
    private final int retryDelay;
    @Value("${delete-application-url}")
    private String deleteApplicaitonUrl;
    private RestTemplate noAuthRestTemplate;
    private RestTemplate authRestTemplate;


    public KubernetesController(RestTemplateBuilder restTemplateBuilder,
                                CustomObjectsApi customObjectsApi,
                                @Value("${github_username}") String github_username,
                                @Value("${github_password}") String github_password,
                                @Value("${proxy-url}") String proxyUrl,
                                @Value("${proxy-port}") int proxyPort,
                                @Value("${isAlive}") String isAliveUrl,
                                @Value("${docker-image-path}") String dockerImagePath,
                                @Value("${max-alive-retries}") int maxRetries,
                                @Value("${alive-retry-delay}") int retryDelay) {

        this.manifestPath = "/nais/{appName}.yaml";
        this.isAliveUri = new UriTemplate(isAliveUrl);
        this.dockerImagePath = dockerImagePath;
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
        this.api = customObjectsApi;


        this.authRestTemplate = restTemplateBuilder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
                        .setRoutePlanner(new DefaultProxyRoutePlanner(new HttpHost(proxyUrl, proxyPort)))
                        .build()))
                .additionalInterceptors(new BasicAuthenticationInterceptor(github_username, github_password))
                .build();

        this.noAuthRestTemplate = restTemplateBuilder.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
                .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(TIMEOUT)
                        .setSocketTimeout(TIMEOUT)
                        .setConnectionRequestTimeout(TIMEOUT)
                        .build())
                .setMaxConnPerRoute(2000)
                .setMaxConnTotal(5000)
                .build())).build();
    }

    public void deployImage(String appName) throws ApiException, InterruptedException {
        Map<String, Object> manifestFile = prepareYaml(appName);

        if (!existsOnCluster(appName)) {
            api.createNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, manifestFile, null);
            log.info("Application \'{}\' created!", appName);
            waitForDeployment(appName);
        } else if (!isAlive(appName)) {
            waitForDeployment(appName);
        }
    }


    public void takedownImage(String appName) throws ApiException, JsonSyntaxException {

        RequestEntity deleteRequest = RequestEntity.delete(new UriTemplate(deleteApplicaitonUrl).expand(NAMESPACE, appName)).build();

        if (existsOnCluster(appName)) {
            V1DeleteOptions deleteOptions = new V1DeleteOptions();

            try {
                api.deleteNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, appName, deleteOptions,
                        null, null, null);

                ResponseEntity<String> result = noAuthRestTemplate.exchange(deleteRequest, String.class);
                log.info("Successfully deleted application \'{}\'", appName);

            } catch (JsonSyntaxException e) { // TODO: When does this happen?

                if (e.getCause() instanceof IllegalStateException) {
                    IllegalStateException ise = (IllegalStateException) e.getCause();
                    if (!Objects.isNull(ise.getMessage()) && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")) {
                        log.info("Successfully deleted application \'{}\'", appName);
                    } else {
                        throw e;
                    }
                } else {
                    throw e;
                }

            }

        } else {

            ResponseEntity<String> result = noAuthRestTemplate.exchange(deleteRequest, String.class);
            log.info("No application named \'{}\' found. Unable to delete.", appName);
            throw new IllegalArgumentException("No application named \'" + appName + "\' found. Unable to delete.");
        }
    }

    public boolean isAlive(String appName) {
        String response = "404";
        try {
            response = noAuthRestTemplate.getForObject(isAliveUri.expand(appName), String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ignored) {
        }
        return "1".equals(response);
    }

    public boolean existsOnCluster(String appName) throws ApiException {
        List<String> applications = listApplicationsOnCluster();
        return applications.contains(appName);
    }


    private List<String> listApplicationsOnCluster() throws ApiException {

        List<String> applications = new ArrayList<>();

        LinkedTreeMap result = (LinkedTreeMap) api.listNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL,
                null, null, null, null);
        ArrayList items = (ArrayList) result.get("items");

        for (Object item : items) {
            LinkedTreeMap app = (LinkedTreeMap) item;
            LinkedTreeMap metadata = (LinkedTreeMap) app.get("metadata");
            String name = (String) metadata.get("name");

            applications.add(name);
        }

        return applications;
    }

    private void waitForDeployment(String appName) throws InterruptedException, ApiException {
        log.info("Checking \'{}\'s deployment status...", appName);
        int numRetries = 0;

        log.info("Waiting for \'{}\' to deploy... (max {} seconds)", appName, (maxRetries * retryDelay));
        while (!isAlive(appName)) {
            if (numRetries < maxRetries) {
                TimeUnit.SECONDS.sleep(retryDelay);
            } else {
                log.error("Application \'{}\' failed to deploy. Terminating...", appName);
                takedownImage(appName);
                return;
            }
            numRetries++;
        }
        log.info("Application \'{}\' deployed successfully!", appName);
    }


    private Map<String, Object> prepareYaml(String appName) {
        Yaml yaml = new Yaml();
        Map<String, Object> manifestFile = yaml.load(
                getClass().getResourceAsStream(manifestPath.replace("{appName}", appName)));

        Map<String, Object> spec = (Map) manifestFile.get("spec");
        String imageBase = spec.get("image").toString();
        String latestImage = imageBase.replace("latest", getApplicationTag(appName).orElse("latest"));
        spec.put("image", latestImage);

        return manifestFile;
    }


    private Optional<String> getApplicationTag(String appName) {
        String apiUrl = "https://api.github.com/graphql";

        QueryObject query = QueryObject.builder().query(getCorrectTagQuery(appName)).build();

        try {
            RequestEntity requestLatestTag = RequestEntity.post(new UriTemplate(apiUrl).expand()).header("Content-Type", "application/json").body(query);
            ResponseEntity<JsonNode> response = authRestTemplate.exchange(requestLatestTag, JsonNode.class);

            if (response.getBody() != null) {
                String tag = response.getBody().findValue("version").asText();
                log.info("Deploying {}, version: {}", appName, tag);
                return Optional.of(tag);
            }
        } catch (Exception e) {
            log.warn("An exception occurred trying to retrieve application tag: {}", e.getMessage());
        }

        log.warn("Could not find tag for application {}.", appName);
        return Optional.empty();
    }

    private String getCorrectTagQuery(String appName) {
        return "query {repository(owner:\"navikt\", name:\"testnorge-syntetiseringspakker\") {packages(names:[\"" + getCorrectGithubPackageName(appName) + "\"] last:1) {nodes {latestVersion{version}} }}}";
    }

    private String getCorrectGithubPackageName(String appName) {
        int bindestrekIndex = appName.lastIndexOf("-");
        String app = appName.substring(bindestrekIndex + 1);
        return "synt_" + app;
    }

}
