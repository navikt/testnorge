package no.nav.registre.syntrest.consumer.domain;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.consumer.command.PostArbeidsforholdHistorikkCommand;
import no.nav.registre.syntrest.consumer.command.PostArbeidsforholdMedTypeCommand;
import no.nav.registre.syntrest.consumer.command.PostArbeidsforholdStartCommand;
import no.nav.registre.syntrest.domain.aareg.amelding.ArbeidsforholdAmelding;
import no.nav.registre.syntrest.domain.aareg.amelding.ArbeidsforholdPeriode;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;

import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilderFactory;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class SyntAmeldingConsumer extends SyntConsumer {

    private static final String REST_CLIENT_EXCEPTION_MESSAGE = "Unexpected Rest Client Exception: {}";
    private final String historikkPath;
    private final String startPath;
    private final String startSpesifikkPath;

    public SyntAmeldingConsumer(
            ApplicationManager applicationManager,
            String appName,
            String uri,
            boolean shutdown,
            WebClient.Builder webClientBuilder,
            UriBuilderFactory uriFactory
    ) throws MalformedURLException {
        super(applicationManager, appName, uri, shutdown, webClientBuilder.baseUrl(uri), uriFactory);
        this.historikkPath = this.url.getPath() + "/arbeidsforhold";
        this.startPath = this.url.getPath() + "/arbeidsforhold/start";
        this.startSpesifikkPath = this.url.getPath() + "/arbeidsforhold/start/%s";
    }

    public List<ArbeidsforholdAmelding> synthesizeArbeidsforholdStart(List<String> datoer) throws ApiException, InterruptedException {
        startApplication();
        try {
            return new PostArbeidsforholdStartCommand(datoer, startPath, webClient).call();
        } catch (RestClientException e) {
            log.error(REST_CLIENT_EXCEPTION_MESSAGE, Arrays.toString(e.getStackTrace()));
            throw e;
        } finally {
            scheduleIfShutdown();
        }
    }

    public ArbeidsforholdAmelding synthesizeArbeidsforholdStart(
            ArbeidsforholdPeriode request,
            String arbeidsforholdType
    ) throws ApiException, InterruptedException {
        startApplication();
        try {
            return new PostArbeidsforholdMedTypeCommand(request, String.format(startSpesifikkPath, arbeidsforholdType), webClient).call();
        } catch (RestClientException e) {
            log.error(REST_CLIENT_EXCEPTION_MESSAGE, Arrays.toString(e.getStackTrace()));
            throw e;
        } finally {
            scheduleIfShutdown();
        }
    }

    public List<ArbeidsforholdAmelding> synthesizeArbeidsforholdHistorikk(
            ArbeidsforholdAmelding tidligereArbeidsforhold
    ) throws ApiException, InterruptedException {
        startApplication();
        try {
            return new PostArbeidsforholdHistorikkCommand(tidligereArbeidsforhold, historikkPath, webClient).call();
        } catch (RestClientException e) {
            log.error(REST_CLIENT_EXCEPTION_MESSAGE, Arrays.toString(e.getStackTrace()));
            throw e;
        }
    }
}
