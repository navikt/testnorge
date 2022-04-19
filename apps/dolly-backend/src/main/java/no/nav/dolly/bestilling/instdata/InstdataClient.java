package no.nav.dolly.bestilling.instdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.instdata.domain.DeleteResponseDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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
            List<String> availEnvironments = instdataConsumer.getMiljoer();

            List<String> environments = new ArrayList<>(List.copyOf(availEnvironments));
            environments.retainAll(bestilling.getEnvironments());

            if (!environments.isEmpty()) {

                List<Instdata> instdataListe = mapperFacade.mapAsList(bestilling.getInstdata(), Instdata.class);
                instdataListe.forEach(instdata ->
                        instdata.setNorskident(dollyPerson.getHovedperson())
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

    @Override
    public void release(List<String> identer) {

        try {
            instdataConsumer.deleteInstdata(identer)
                    .subscribe(response -> log.info("Slettet antall {} identer fra Instdata",
                            response.stream().collect(Collectors.groupingBy(DeleteResponseDTO::getEnvironment))
                                    .entrySet()
                                    .stream()
                                    .findFirst()
                                    .orElse(Map.entry("miljoe", Collections.emptyList()))
                                    .getValue().size()));

        } catch (RuntimeException e) {

            log.error("Feilet å slette identer fra Instdata: ", identer.stream().collect(Collectors.joining(", ")), e);
        }
    }

    private List<Instdata> filterInstdata(List<Instdata> instdataRequest, String miljoe) {

        ResponseEntity<List<Instdata>> eksisterendeInstdata = instdataConsumer.getInstdata(instdataRequest.get(0).getNorskident(), miljoe);

        if (eksisterendeInstdata.hasBody() && !eksisterendeInstdata.getBody().isEmpty()) {

            return instdataRequest.stream().filter(request -> eksisterendeInstdata.getBody().stream()
                            .noneMatch(eksisterende -> eksisterende.equals(request)))
                    .collect(Collectors.toList());
        } else {

            return instdataRequest;
        }
    }

    private String postInstdata(boolean isNewOpphold, List<Instdata> instdata, String environment) {

        StringBuilder status = new StringBuilder();

        List<Instdata> newInstdata = isNewOpphold ? instdata : filterInstdata(instdata, environment);

        if (!newInstdata.isEmpty()) {
            ResponseEntity<List<InstdataResponse>> response = instdataConsumer.postInstdata(newInstdata, environment);

            if (response.hasBody()) {

                for (int i = 0; i < response.getBody().size(); i++) {
                    status.append(',')
                            .append(environment)
                            .append(':')
                            .append("opphold=")
                            .append(i + 1)
                            .append('$')
                            .append(CREATED.equals(response.getBody().get(i).getStatus()) || OK.equals(response.getBody().get(i).getStatus()) ? OK_RESULT :
                                    errorStatusDecoder.getErrorText(response.getBody().get(i).getStatus(),
                                            response.getBody().get(i).getFeilmelding()));
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
