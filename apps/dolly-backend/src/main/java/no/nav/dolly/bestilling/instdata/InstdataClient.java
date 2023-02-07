package no.nav.dolly.bestilling.instdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.instdata.domain.DeleteResponseDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
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
    public Flux<Void> gjenopprett(@Nullable Bruker ignored, RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getInstdata().isEmpty()) {

            StringBuilder status = new StringBuilder();
            List<String> availEnvironments = instdataConsumer.getMiljoer();

            Set<String> environments = new HashSet<>(Set.copyOf(availEnvironments));
            environments.retainAll(bestilling.getEnvironments().isEmpty() ? environments : bestilling.getEnvironments());

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
                                .append(errorStatusDecoder.decodeThrowable(e));

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
        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

        instdataConsumer.deleteInstdata(identer)
                .subscribe(response -> log.info("Slettet antall {} identer fra Instdata",
                        response.stream().collect(Collectors.groupingBy(DeleteResponseDTO::getEnvironment))
                                .entrySet()
                                .stream()
                                .findFirst()
                                .orElse(Map.entry("miljoe", Collections.emptyList()))
                                .getValue().size()));
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getInstdata()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getInstdataStatus()));
    }

    private List<Instdata> filterInstdata(List<Instdata> instdataRequest, String miljoe) {

        ResponseEntity<List<Instdata>> eksisterendeInstdata = instdataConsumer.getInstdata(instdataRequest.get(0).getNorskident(), miljoe);

        if (eksisterendeInstdata.hasBody() && !eksisterendeInstdata.getBody().isEmpty()) {

            return instdataRequest
                    .stream()
                    .filter(request -> eksisterendeInstdata
                            .getBody()
                            .stream()
                            .noneMatch(eksisterende -> eksisterende.equals(request)))
                    .toList();
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
