package no.nav.dolly.bestilling.instdata;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstdataClient implements ClientRegister {

    private static final String OK_RESULT = "OK";

    private final MapperFacade mapperFacade;
    private final InstdataConsumer instdataConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getInstdata().isEmpty()) {

            StringBuilder status = new StringBuilder();
            List<String> availEnvironments = new ArrayList<>(List.of(instdataConsumer.getMiljoer()));

            List<String> environments = new ArrayList<>(List.copyOf(availEnvironments));
            environments.retainAll(bestilling.getEnvironments());

            if (!environments.isEmpty()) {

                List<Instdata> instdataListe = mapperFacade.mapAsList(bestilling.getInstdata(), Instdata.class);
                instdataListe.forEach(instdata ->
                        instdata.setPersonident(dollyPerson.getHovedperson())
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

                        log.error("Feilet å legge til opphold for person: {} til INST miljø: {}", dollyPerson.getHovedperson(), environment, e);
                    }
                });
            }

            List<String> notSupportedEnvironments = new ArrayList<>(bestilling.getEnvironments());
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

            return instdataRequest;
        }
    }

    @Override
    public void release(List<String> identer) {

        List<String> environments = List.of(instdataConsumer.getMiljoer());
        environments.forEach(environment ->
                identer.forEach(ident -> instdataConsumer.deleteInstdata(ident, environment))
        );
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
