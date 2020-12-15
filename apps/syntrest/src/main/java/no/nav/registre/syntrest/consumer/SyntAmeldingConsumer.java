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
import java.util.List;

@Slf4j
public class SyntAmeldingConsumer extends SyntConsumer {
    private final WebClient webClient;

    public SyntAmeldingConsumer(ApplicationManager applicationManager, String appName, String synthAmeldingUrl) {
        super(applicationManager, appName);
        this.webClient = WebClient.builder().baseUrl(synthAmeldingUrl).build();
    }

    public ArbeidsforholdAmelding synthesizeArbeidsforhold(ArbeidsforholdAmelding tidligereArbeidsforhold, String syntAmeldingUrlPath) {
        try {
            applicationManager.startApplication(this);
        } catch (ApiException | InterruptedException e) {
            log.error("Could not start synth package {}: {}", this.appName, e.getMessage());
            return null;
        }

        try {
            return new PostArbeidsforholdCommand(tidligereArbeidsforhold, syntAmeldingUrlPath, webClient).call();
        } catch (RestClientException e) {
            log.error("Unexpected Rest Client Exception: {}", Arrays.toString(e.getStackTrace()));
            throw e;
        } finally {
            scheduleShutdown();
        }
    }

    public List<ArbeidsforholdAmelding> synthesizeArbeidsforholdStart(List<String> datoer, String url) {

        try {
            applicationManager.startApplication(this);
        } catch (ApiException | InterruptedException e) {
            log.error("Could not start synth package {}: {}", this.appName, e.getMessage());
            return null;
        }

        try {
            return new PostArbeidsforholdStartCommand(datoer, url, webClient).call();
        } catch (RestClientException e) {
            log.error("Unexpected Rest Client Exception: {}", Arrays.toString(e.getStackTrace()));
            throw e;
        } finally {
            scheduleShutdown();
        }
    }

    public List<ArbeidsforholdAmelding> synthesizeArbeidsforholdHistorikk(ArbeidsforholdAmelding tidligereArbeidsforhold, String syntAmeldingUrlPath) {
        try {
            applicationManager.startApplication(this);
        } catch (ApiException | InterruptedException e) {
            log.error("Could not start synth package {}: {}", this.appName, e.getMessage());
            return null;
        }

        try {
            return new PostArbeidsforholdHistorikkCommand(tidligereArbeidsforhold, syntAmeldingUrlPath, webClient).call();
        } catch (RestClientException e) {
            log.error("Unexpected Rest Client Exception: {}", Arrays.toString(e.getStackTrace()));
            throw e;
        } finally {
            scheduleShutdown();
        }
    }
}
