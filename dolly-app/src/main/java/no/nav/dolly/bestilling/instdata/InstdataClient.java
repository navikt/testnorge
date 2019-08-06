package no.nav.dolly.bestilling.instdata;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.inst.InstdataInstitusjonstype;
import no.nav.dolly.domain.resultset.inst.InstdataKategori;
import no.nav.dolly.domain.resultset.inst.InstdataKilde;

@Slf4j
@Service
public class InstdataClient implements ClientRegister {

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private InstdataConsumer instdataConsumer;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (nonNull(bestilling.getInstdata())) {

            StringBuilder status = new StringBuilder();

            ResponseEntity<List> envResponse = ResponseEntity.created(URI.create("")).body(newArrayList("q0", "q2")); //TODO replace by call to endpoint when avail
            List<String> environments = envResponse.hasBody() ? envResponse.getBody() : emptyList();

            List<String> availEnvironments = new ArrayList(environments);

            availEnvironments.retainAll(bestilling.getEnvironments());

            if (!availEnvironments.isEmpty()) {

                availEnvironments.forEach(environment -> {

                    if (deleteInstdata(norskIdent.getIdent(), environment, status)) {

                        List<Instdata> instdataListe = mapperFacade.mapAsList(bestilling.getInstdata(), Instdata.class);
                        instdataListe.forEach(instdata -> {
                            instdata.setPersonident(norskIdent.getIdent());

                            instdata.setKategori(nullcheckSetDefaultValue(instdata.getKategori(), decideKategori(instdata.getInstitusjonstype())));
                            instdata.setKilde(nullcheckSetDefaultValue(instdata.getKilde(), decideKilde(instdata.getInstitusjonstype())));
                            instdata.setOverfoert(nullcheckSetDefaultValue(instdata.getOverfoert(), false));
                            instdata.setTssEksternId(nullcheckSetDefaultValue(instdata.getTssEksternId(), decideTssEksternId(instdata.getInstitusjonstype())));
                        });

                        postInstdata(norskIdent.getIdent(), instdataListe, environment, status);
                    }
                });
            }

            List<String> notSupportedEnvironments = new ArrayList(bestilling.getEnvironments());
            notSupportedEnvironments.removeAll(environments);
            notSupportedEnvironments.forEach(environment ->
                    status.append(',')
                            .append(environment)
                            .append(":Feil: Miljø ikke støttet"));

            progress.setInstdataStatus(status.substring(1));
        }
    }

    private boolean deleteInstdata(String ident, String environment, StringBuilder status) {

        try {
            ResponseEntity<InstdataResponse[]> response = instdataConsumer.deleteInstdata(ident, environment);

            if (response.hasBody() && response.getBody().length > 0) {
                if (HttpStatus.NOT_FOUND.equals(response.getBody()[0].getStatus())
                        || HttpStatus.OK.equals(response.getBody()[0].getStatus())) {

                    return true;
                } else {
                    status.append(',')
                            .append(environment)
                            .append(':')
                            .append(getErrorText(response.getBody()[0].getStatus(), response.getBody()[0].getFeilmelding()));
                }
            }
        } catch (RuntimeException e) {
            status.append(',')
                    .append(environment)
                    .append(':');
            appendErrorText(status, e);
            log.error("Feilet å slette person: {}, i INST miljø: {}", ident, environment, e);
        }
        return false;
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
                            .append(HttpStatus.CREATED.value() == response.getBody()[i].getStatus().value() ? "OK" :
                                    getErrorText(response.getBody()[i].getStatus(), response.getBody()[i].getFeilmelding()));
                }
            }

        } catch (RuntimeException e) {

            status.append(',')
                    .append(environment)
                    .append(':');
            appendErrorText(status, e);

            log.error("Feilet å legge inn person: {} til INST miljø: {}", ident, environment, e);
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

    private static void appendErrorText(StringBuilder status, RuntimeException e) {
        status.append("Feil: ")
                .append(e.getMessage());

        if (e instanceof HttpClientErrorException) {
            status.append(" (")
                    .append(encodeErrorStatus(((HttpClientErrorException) e).getResponseBodyAsString()))
                    .append(')');
        }
    }

    private String getErrorText(HttpStatus errorStatus, String errorMsg) {

        StringBuilder status = new StringBuilder()
                .append("Feil: ")
                .append(errorStatus.value())
                .append(" (")
                .append(errorStatus.getReasonPhrase())
                .append(") ");

        try {

            status.append(encodeErrorStatus(errorMsg.contains("{") ? (String) objectMapper.readValue(errorMsg, Map.class).get("message") : errorMsg));
        } catch (IOException e) {

            log.warn("Parsing av feilmelding fra INST-adapter feilet: ", e);
        }

        return status.toString();
    }

    private static String encodeErrorStatus(String toBoEncoded) {
        return toBoEncoded.replaceAll(",", "&").replaceAll(":", "=");
    }
}
