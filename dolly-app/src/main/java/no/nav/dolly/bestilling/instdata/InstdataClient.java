package no.nav.dolly.bestilling.instdata;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.inst.InstdataInstitusjonstype;
import no.nav.dolly.domain.resultset.inst.InstdataKategori;
import no.nav.dolly.domain.resultset.inst.InstdataKilde;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;

@Slf4j
@Service
public class InstdataClient implements ClientRegister {

    public static final String OK_RESULT = "OK";
    private static final String[] DEFAULT_ENV = { "q2" };

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private InstdataConsumer instdataConsumer;

    @Autowired
    private ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyBestillingRequest bestilling, TpsPerson tpsPerson, BestillingProgress progress) {

        if (nonNull(bestilling.getInstdata())) {

            StringBuilder status = new StringBuilder();
            List<String> availEnvironments = getEnvironments();

            List<String> environments = newArrayList(availEnvironments);
            environments.retainAll(bestilling.getEnvironments());

            if (!environments.isEmpty()) {

                environments.forEach(environment -> {
                    deleteInstdata(tpsPerson.getHovedperson(), environment);

                    List<Instdata> instdataListe = mapperFacade.mapAsList(bestilling.getInstdata(), Instdata.class);
                    instdataListe.forEach(instdata -> {
                        instdata.setPersonident(tpsPerson.getHovedperson());
                        instdata.setKategori(nullcheckSetDefaultValue(instdata.getKategori(), decideKategori(instdata.getInstitusjonstype())));
                        instdata.setKilde(nullcheckSetDefaultValue(instdata.getKilde(), decideKilde(instdata.getInstitusjonstype())));
                        instdata.setOverfoert(nullcheckSetDefaultValue(instdata.getOverfoert(), false));
                        instdata.setTssEksternId(nullcheckSetDefaultValue(instdata.getTssEksternId(), decideTssEksternId(instdata.getInstitusjonstype())));
                    });

                    postInstdata(tpsPerson.getHovedperson(), instdataListe, environment, status);
                });
            }

            List<String> notSupportedEnvironments = new ArrayList(bestilling.getEnvironments());
            notSupportedEnvironments.removeAll(availEnvironments);
            notSupportedEnvironments.forEach(environment ->
                    status.append(',')
                            .append(environment)
                            .append(":Feil: Miljø ikke støttet"));

            progress.setInstdataStatus(status.substring(1));
        }
    }

    @Override
    public void release(List<String> identer) {

        List<String> environments = getEnvironments();
        environments.forEach(environment ->
                identer.forEach(ident -> deleteInstdata(ident, environment))
        );
    }

    private List<String> getEnvironments() {

        try {
            ResponseEntity<String[]> envResponse = instdataConsumer.getMiljoer();
            return newArrayList(envResponse.hasBody() ? envResponse.getBody() : DEFAULT_ENV);

        } catch (RuntimeException e) {
            log.error("Kunne ikke lese fra endepunkt for å hente miljøer: {} ", e.getMessage(), e);
            return newArrayList(DEFAULT_ENV);
        }
    }

    private void deleteInstdata(String ident, String environment) {

        try {
            ResponseEntity<InstdataResponse[]> response = instdataConsumer.deleteInstdata(ident, environment);

            if (!response.hasBody() ||
                    (!NOT_FOUND.equals(response.getBody()[0].getStatus()) &&
                            !OK.equals(response.getBody()[0].getStatus()))) {

                log.error("Feilet å slette person: {}, i INST miljø: {}", ident, environment);
            }

        } catch (HttpClientErrorException e) {

            if (!NOT_FOUND.equals(e.getStatusCode())) {
                log.error("Feilet å slette person: {}, i INST miljø: {}", ident, environment, e);
            }
        } catch (RuntimeException e) {

            log.error("Feilet å slette person: {}, i INST miljø: {}", ident, environment, e);
        }
    }

    private void postInstdata(String ident, List<Instdata> instdata, String environment, StringBuilder status) {

        try {
            ResponseEntity<InstdataResponse[]> response = instdataConsumer.postInstdata(instdata, environment);

            if (response.hasBody()) {

                for (int i = 0; i < response.getBody().length; i++) {
                    status.append(',')
                            .append(environment)
                            .append(':')
                            .append("opphold=")
                            .append(i + 1)
                            .append('$')
                            .append(CREATED.equals(response.getBody()[i].getStatus()) ? OK_RESULT :
                                    errorStatusDecoder.getErrorText(response.getBody()[i].getStatus(), response.getBody()[i].getFeilmelding()));
                }
            }

        } catch (RuntimeException re) {

            status.append(',')
                    .append(environment)
                    .append(':')
                    .append(errorStatusDecoder.decodeRuntimeException(re));

            log.error("Feilet å legge inn person: {} til INST miljø: {}", ident, environment, re);
        }
    }

    private static InstdataKategori decideKategori(InstdataInstitusjonstype type) {

        switch (type) {
        case AS:
            return InstdataKategori.A;
        case FO:
            return InstdataKategori.S;
        case HS:
        default:
            return InstdataKategori.R;
        }
    }

    private static InstdataKilde decideKilde(InstdataInstitusjonstype type) {

        switch (type) {
        case AS:
            return InstdataKilde.PP01;
        case FO:
            return InstdataKilde.IT;
        case HS:
        default:
            return InstdataKilde.INST;
        }
    }

    private static String decideTssEksternId(InstdataInstitusjonstype type) {

        switch (type) {
        case AS:
            return "80000464106"; // ADAMSTUEN SYKEHJEM
        case FO:
            return "80000465653"; // INDRE ØSTFOLD FENGSEL
        case HS:
        default:
            return "80000464241"; // HELGELANDSSYKEHUSET HF
        }
    }
}
