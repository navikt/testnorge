package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.consumer.command.PostArbeidsforholdCommand;
import no.nav.registre.syntrest.consumer.command.PostArbeidsforholdHistorikkCommand;
import no.nav.registre.syntrest.consumer.command.PostArbeidsforholdStartCommand;
import no.nav.registre.syntrest.domain.amelding.ArbeidsforholdAmelding;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;

import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class SyntAmeldingConsumer extends SyntConsumer {
    private final WebClient webClient;

    private static final String WEB_CLIENT_EXCEPTION_MESSAGE = "Unexpected Web Client Exception: {}";

    public SyntAmeldingConsumer(ApplicationManager applicationManager, String appName, String synthAmeldingUrl) {
        super(applicationManager, appName);
        this.webClient = WebClient.builder().baseUrl(synthAmeldingUrl).build();
    }

    public ArbeidsforholdAmelding synthesizeArbeidsforhold(ArbeidsforholdAmelding tidligereArbeidsforhold, String syntAmeldingUrlPath) {
        try {
            startSyntApplication();
        } catch (ApiException e) {
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

        try {
            return new PostArbeidsforholdCommand(tidligereArbeidsforhold, syntAmeldingUrlPath, webClient).call();
        } catch (RestClientException e) {
            log.error(WEB_CLIENT_EXCEPTION_MESSAGE, Arrays.toString(e.getStackTrace()));
            throw e;
        } finally {
            scheduleShutdown();
        }
    }

    public List<ArbeidsforholdAmelding> synthesizeArbeidsforholdStart(List<String> datoer, String url) {
        try {
            startSyntApplication();
        } catch (ApiException e) {
            return Collections.emptyList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }

        try {
            return new PostArbeidsforholdStartCommand(datoer, url, webClient).call();
        } catch (RestClientException e) {
            log.error(WEB_CLIENT_EXCEPTION_MESSAGE, Arrays.toString(e.getStackTrace()));
            throw e;
        } finally {
            scheduleShutdown();
        }
    }

    public List<ArbeidsforholdAmelding> synthesizeArbeidsforholdHistorikk(ArbeidsforholdAmelding tidligereArbeidsforhold, String syntAmeldingUrlPath) {
        try {
            startSyntApplication();
        } catch (ApiException e) {
            return Collections.emptyList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }

        try {
            return new PostArbeidsforholdHistorikkCommand(tidligereArbeidsforhold, syntAmeldingUrlPath, webClient).call();
        } catch (RestClientException e) {
            log.error(WEB_CLIENT_EXCEPTION_MESSAGE, Arrays.toString(e.getStackTrace()));
            throw e;
        } finally {
            scheduleShutdown();
        }
    }

    private void startSyntApplication() throws InterruptedException, ApiException {
        try {
            applicationManager.startApplication(this);
        } catch (ApiException | InterruptedException e) {
            log.error("Could not start synth package {}: {}", this.appName, e.getMessage());
            throw e;
        }
    }
}
