package no.nav.registre.syntrest.consumer.domain;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.consumer.command.PostArbeidsforholdHistorikkCommand;
import no.nav.registre.syntrest.consumer.command.PostArbeidsforholdStartCommand;
import no.nav.registre.syntrest.domain.aareg.amelding.ArbeidsforholdAmelding;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;

import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class SyntAmeldingConsumer extends SyntConsumer {
    private final WebClient webClient;

    private static final String REST_CLIENT_EXCEPTION_MESSAGE = "Unexpected Rest Client Exception: {}";

    public SyntAmeldingConsumer(ApplicationManager applicationManager, String appName, String uri, boolean shutdown) {
        super(applicationManager, appName, uri, shutdown);
        this.webClient = WebClient.builder().baseUrl(uri).build();
    }

    public List<ArbeidsforholdAmelding> synthesizeArbeidsforholdStart(List<String> datoer, String url) throws ApiException, InterruptedException {
        startApplication();
        try {
            return new PostArbeidsforholdStartCommand(datoer, url, webClient).call();
        } catch (RestClientException e) {
            log.error(REST_CLIENT_EXCEPTION_MESSAGE, Arrays.toString(e.getStackTrace()));
            throw e;
        } finally {
            scheduleIfShutdown();
        }
    }

    public List<ArbeidsforholdAmelding> synthesizeArbeidsforholdHistorikk(
            ArbeidsforholdAmelding tidligereArbeidsforhold,
            String syntAmeldingUrlPath
    ) throws ApiException, InterruptedException {
        startApplication();
        try {
            return new PostArbeidsforholdHistorikkCommand(tidligereArbeidsforhold, syntAmeldingUrlPath, webClient).call();
        } catch (RestClientException e) {
            log.error(REST_CLIENT_EXCEPTION_MESSAGE, Arrays.toString(e.getStackTrace()));
            throw e;
        }
    }
}
