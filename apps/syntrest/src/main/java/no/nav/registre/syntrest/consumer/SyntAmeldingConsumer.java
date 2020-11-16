package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.consumer.command.PostArbeidsforholdCommand;
import no.nav.registre.syntrest.consumer.command.PostArbeidsforholdStartCommand;
import no.nav.registre.syntrest.domain.amelding.Arbeidsforhold;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class SyntAmeldingConsumer extends SyntConsumer{
    private final WebClient webClient;

    public SyntAmeldingConsumer(ApplicationManager applicationManager, String appName, String synthAmeldingUrl) {
        super(applicationManager, appName);
        this.webClient = WebClient.builder().baseUrl(synthAmeldingUrl).build();
    }

    public Arbeidsforhold synthesizeArbeidsforhold(Arbeidsforhold tidligereArbeidsforhold, String syntAmeldingUrlPath ){
        try {
            log.info("Starting synth package {}...", this.appName);
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

    public List<Arbeidsforhold> synthesizeArbeidsforholdStart(List<String> datoer, String url){

        try {
            log.info("Starting synth package {}...", this.appName);
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
}
