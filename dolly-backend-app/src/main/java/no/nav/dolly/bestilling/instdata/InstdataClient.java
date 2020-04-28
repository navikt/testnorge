package no.nav.dolly.bestilling.instdata;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.instdata.domain.DeleteResponse;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.Timed;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstdataClient implements ClientRegister {

    private static final String OK_RESULT = "OK";
    private static final String DELETE_ERROR = "Feilet å slette person: {}, i INST miljø: {}";

    private final MapperFacade mapperFacade;
    private final InstdataConsumer instdataConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final ObjectMapper objectMapper;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getInstdata().isEmpty()) {

            StringBuilder status = new StringBuilder();
            List<String> availEnvironments = newArrayList(instdataConsumer.getMiljoer());

            List<String> environments = newArrayList(availEnvironments);
            environments.retainAll(bestilling.getEnvironments());

            if (!environments.isEmpty()) {

                List<Instdata> instdataListe = mapperFacade.mapAsList(bestilling.getInstdata(), Instdata.class);
                instdataListe.forEach(instdata ->
                        instdata.setPersonident(tpsPerson.getHovedperson())
                );

                environments.forEach(environment -> {

                    try {
                        String postStatus = postInstdata(isOpprettEndre, instdataListe, environment);
                        status.append(postStatus);

                    } catch (RuntimeException e) {
                        status.append(',')
                                .append(environment)
                                .append(':')
                                .append(errorStatusDecoder.decodeRuntimeException(e));

                        log.error("Feilet å legge til opphold for person: {} til INST miljø: {}", tpsPerson.getHovedperson(), environment, e);
                    }
                });
            }

            List<String> notSupportedEnvironments = new ArrayList(bestilling.getEnvironments());
            notSupportedEnvironments.removeAll(availEnvironments);
            notSupportedEnvironments.forEach(environment ->
                    status.append(',')
                            .append(environment)
                            .append(":Feil: Miljø ikke støttet"));

            progress.setInstdataStatus(status.length() > 1 ? status.substring(1) : null);
        }
    }

    private List<Instdata> filterInstdata(List<Instdata> instdataRequest, String miljoe) {

        ResponseEntity<Instdata[]> eksisterendeInstdata = instdataConsumer.getInstdata(instdataRequest.get(0).getPersonident(), miljoe);

        if (eksisterendeInstdata.hasBody() && eksisterendeInstdata.getBody().length > 0) {

            return instdataRequest.stream().filter(request -> Arrays.stream(eksisterendeInstdata.getBody())
                    .noneMatch(eksisterende -> eksisterende.equals(request)))
                    .collect(Collectors.toList());
        } else {

            return emptyList();
        }
    }

    @Override
    public void release(List<String> identer) {

        List<String> environments = newArrayList(instdataConsumer.getMiljoer());
        environments.forEach(environment ->
                identer.forEach(ident -> deleteInstdata(ident, environment))
        );
    }

    @Timed(name = "providers", tags = { "operation", "inst_deleteInstdata" })
    public void deleteInstdata(String ident, String environment) {

        try {
            ResponseEntity<DeleteResponse> response = instdataConsumer.deleteInstdata(ident, environment);

            if (!response.hasBody() ||
                    (!NOT_FOUND.equals(response.getBody().getStatus()) &&
                            !OK.equals(response.getBody().getStatus()))) {

                log.error(DELETE_ERROR, ident, environment);
            }

        } catch (HttpClientErrorException e) {

            if (!NOT_FOUND.equals(e.getStatusCode())) {
                log.error(DELETE_ERROR, ident, environment, e);
            }
        } catch (RuntimeException e) {

            log.error(DELETE_ERROR, ident, environment, e);
        }
    }

    private String postInstdata(boolean isNewOpphold, List<Instdata> instdata, String environment) {

        StringBuilder status = new StringBuilder();

        List<Instdata> newInstdata = isNewOpphold ? instdata : filterInstdata(instdata, environment);

        if (!newInstdata.isEmpty()) {
            ResponseEntity<InstdataResponse[]> response = instdataConsumer.postInstdata(newInstdata, environment);

            if (response.hasBody()) {

                for (int i = 0; i < response.getBody().length; i++) {
                    status.append(',')
                            .append(environment)
                            .append(':')
                            .append("opphold=")
                            .append(i + 1)
                            .append('$')
                            .append(CREATED.equals(response.getBody()[i].getStatus()) ? OK_RESULT :
                                    errorStatusDecoder.getErrorText(response.getBody()[i].getStatus(),
                                            response.getBody()[i].getFeilmelding()));
                }
            }

        } else {
            status.append(',')
                    .append(environment)
                    .append(":opphold=1$OK");
        }

        return status.toString();
    }
}
