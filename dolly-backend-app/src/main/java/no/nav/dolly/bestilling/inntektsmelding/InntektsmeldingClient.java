package no.nav.dolly.bestilling.inntektsmelding;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektsmelding.domain.Inntektsmelding;
import no.nav.dolly.bestilling.instdata.InstdataResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.Timed;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektsmeldingClient implements ClientRegister {

    private final InntektsmeldingConsumer inntektsmeldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    @Timed(name = "providers", tags = { "operation", "gjenopprettInntektsmelding" })
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getInntektsmelding())) {

            StringBuilder status = new StringBuilder();
            List<String> availEnvironments = inntektsmeldingConsumer.getMiljoer();

            List<String> environments = mapperFacade.map(availEnvironments, List.class);
            environments.retainAll(bestilling.getEnvironments());

            if (!environments.isEmpty()) {

                environments.forEach(environment -> {
                    if (!isOpprettEndre) {
                        deleteInstdata(tpsPerson.getHovedperson(), environment);
                    }

                    Inntektsmelding inntektsmelding = mapperFacade.map(bestilling.getInntektsmelding(), Inntektsmelding.class);
                    inntektsmelding.setArbeidstakerFnr(tpsPerson.getHovedperson());
                    inntektsmelding.setMiljoe(environment);

                    postInntektsmelding(inntektsmelding, status);
                });
            }

            List<String> notSupportedEnvironments = mapperFacade.map(bestilling.getEnvironments(), List.class);
            notSupportedEnvironments.removeAll(availEnvironments);
            notSupportedEnvironments.forEach(environment ->
                    status.append(',')
                            .append(environment)
                            .append(":Feil: Miljø ikke støttet"));

            progress.setInstdataStatus(status.length() > 1 ? status.substring(1) : null);
        }
    }

    @Override
    public void release(List<String> identer) {

        List<String> environments = inntektsmeldingConsumer.getMiljoer();
        environments.forEach(environment ->
                identer.forEach(ident -> deleteInstdata(ident, environment))
        );
    }

    private void deleteInstdata(String ident, String environment) {

        try {
            ResponseEntity<InstdataResponse[]> response = inntektsmeldingConsumer.deleteInntektsmelding(ident, environment);

            if (!response.hasBody() ||
                    (!NOT_FOUND.equals(response.getBody()[0].getStatus()) &&
                            !OK.equals(response.getBody()[0].getStatus()))) {

                log.error("Feilet å slette person: {}, Inntektsmelding i miljø: {}", ident, environment);
            }

        } catch (HttpClientErrorException e) {

            if (!NOT_FOUND.equals(e.getStatusCode())) {
                log.error("Feilet å slette person: {}, Inntektsmelding i miljø: {}", ident, environment, e);
            }
        } catch (RuntimeException e) {

            log.error("Feilet å slette person: {}, Inntektsmelding i miljø: {}", ident, environment, e);
        }
    }

    private void postInntektsmelding(Inntektsmelding inntektsmelding, StringBuilder status) {

        try {
            inntektsmeldingConsumer.postInntektsmelding(inntektsmelding);

            status.append(',')
                    .append(inntektsmelding.getMiljoe())
                    .append(":OK");

        } catch (RuntimeException re) {

            status.append(',')
                    .append(inntektsmelding.getMiljoe())
                    .append(':')
                    .append(errorStatusDecoder.decodeRuntimeException(re));

            log.error("Feilet å legge inn person: {} til Inntektsmelding miljø: {}",
                    inntektsmelding.getArbeidstakerFnr(), inntektsmelding.getMiljoe(), re);
        }
    }
}
