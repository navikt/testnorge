package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

@Slf4j
public abstract class SyntConsumer {
    /**
     * The SyntConsumer class talks to the synt-package once it has been deployed to the cluster.
     */

    protected final ApplicationManager applicationManager;
    protected final WebClient webClient;
    protected final boolean shutdown;
    protected final String appName;

    protected URL url;

    @Value("${synth-package-unused-uptime}")
    private long shutdownTimeDelaySeconds;


    public SyntConsumer(ApplicationManager applicationManager, String name, String uri, boolean shutdown, WebClient.Builder webClientBuilder) throws MalformedURLException {
        this.applicationManager = applicationManager;
        this.appName = name;
        this.shutdown = shutdown;
        this.url = new URL(uri);

        var baseUrl = this.url.getProtocol() + "://" + this.url.getAuthority();
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    protected void scheduleIfShutdown() {
        if (shutdown) {
            applicationManager.scheduleShutdown(this, shutdownTimeDelaySeconds);
        }
    }

    public void shutdownApplication() {
            applicationManager.shutdownApplication(appName);
    }

    public void startApplication() throws InterruptedException, ApiException {
        try {
            applicationManager.startApplication(this);
        } catch(InterruptedException | ApiException e) {
            log.error("Could not access synth package {}: \n{}", appName, Arrays.toString(e.getStackTrace()));
            throw e;
        }
    }

    public String getAppName() {
        return this.appName;
    }

    public void addQueryParameter(String parameterName, Object parameterValue) throws MalformedURLException {
        var querys = this.url.getQuery();
        var paramSeparator = querys.contains("?") ? "&" : "?";
        var newQueyParam = paramSeparator + parameterName + "=" + parameterValue.toString();
        this.url = new URL(this.url.toString() + newQueyParam);
    }
}
