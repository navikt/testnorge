package no.nav.dolly.bestilling.pdlforvalter;

import static java.time.LocalDate.now;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.NullcheckUtil.blankcheckSetDefaultValue;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.pdlforvalter.Pdldata;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;

@Slf4j
@Service
public class PdlForvalterClient implements ClientRegister {

    public static final String KONTAKTINFORMASJON_DOEDSBO = "KontaktinformasjonForDoedsbo";
    public static final String UTENLANDS_IDENTIFIKASJONSNUMMER = "UtenlandskIdentifikasjonsnummer";
    public static final String FALSK_IDENTITET = "FalskIdentitet";
    public static final String PDL_FORVALTER = "PdlForvalter";
    private static final String DELETE_IDENT = "DeleteIdent";
    private static final String KILDE = "Dolly";
    private static final String SYNTH_ENV = "q2";
    private static final String HENDELSE_ID = "hendelseId";

    @Autowired
    private PdlForvalterRestConsumer pdlForvalterRestConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    @Override public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (nonNull(bestilling.getPdlforvalter())) {

            StringBuilder status = new StringBuilder();

            if (bestilling.getEnvironments().contains(SYNTH_ENV)) {

                Pdldata pdldata = mapperFacade.map(bestilling.getPdlforvalter(), Pdldata.class);

                sendDeleteIdent(norskIdent, status);
                sendUtenlandsid(pdldata, norskIdent, status);
                sendDoedsbo(pdldata, norskIdent, status);
                sendFalskIdentitet(pdldata, norskIdent, status);

            } else {

                status.append('$')
                        .append(PDL_FORVALTER)
                        .append("&Feil: Bestilling ble ikke sendt til PdlForvalter da miljø '")
                        .append(SYNTH_ENV)
                        .append("' ikke er valgt");
            }

            progress.setPdlforvalterStatus(status.substring(1));
        }
    }

    private void sendUtenlandsid(Pdldata pdldata, NorskIdent norskIdent, StringBuilder status) {

        if (nonNull(pdldata) && nonNull(pdldata.getUtenlandskIdentifikasjonsnummer())) {
            try {
                appendName(UTENLANDS_IDENTIFIKASJONSNUMMER, status);

                PdlUtenlandskIdentifikasjonsnummer utenlandskId = pdldata.getUtenlandskIdentifikasjonsnummer();
                utenlandskId.setKilde(nullcheckSetDefaultValue(utenlandskId.getKilde(), KILDE));

                ResponseEntity<JsonNode> response =
                        pdlForvalterRestConsumer.postUtenlandskIdentifikasjonsnummer(utenlandskId, norskIdent.getIdent());

                appendOkStatus(response.getBody(), status);

            } catch (RuntimeException exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendDoedsbo(Pdldata pdldata, NorskIdent norskIdent, StringBuilder status) {

        if (nonNull(pdldata) && nonNull(pdldata.getKontaktinformasjonForDoedsbo())) {
            try {
                appendName(KONTAKTINFORMASJON_DOEDSBO, status);

                PdlKontaktinformasjonForDoedsbo kontaktinformasjon = pdldata.getKontaktinformasjonForDoedsbo();
                kontaktinformasjon.setKilde(KILDE);
                kontaktinformasjon.setUtstedtDato(nullcheckSetDefaultValue(kontaktinformasjon.getUtstedtDato(), now()));
                kontaktinformasjon.setLandkode(blankcheckSetDefaultValue(kontaktinformasjon.getLandkode(), "NOR"));

                ResponseEntity<JsonNode> response =
                        pdlForvalterRestConsumer.postKontaktinformasjonForDoedsbo(kontaktinformasjon, norskIdent.getIdent());

                appendOkStatus(response.getBody(), status);

            } catch (RuntimeException exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendFalskIdentitet(Pdldata pdldata, NorskIdent norskIdent, StringBuilder status) {

        if (nonNull(pdldata) && nonNull(pdldata.getFalskIdentitet())) {
            try {
                appendName(FALSK_IDENTITET, status);

                PdlFalskIdentitet falskIdentitet = pdldata.getFalskIdentitet();
                falskIdentitet.setErFalsk(nullcheckSetDefaultValue(falskIdentitet.getErFalsk(), true));
                falskIdentitet.setKilde(nullcheckSetDefaultValue(falskIdentitet.getKilde(), KILDE));

                ResponseEntity<JsonNode> response =
                        pdlForvalterRestConsumer.postFalskIdentitet(falskIdentitet, norskIdent.getIdent());

                appendOkStatus(response.getBody(), status);

            } catch (RuntimeException exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendDeleteIdent(NorskIdent norskIdent, StringBuilder status) {

        try {
            appendName(DELETE_IDENT, status);
            ResponseEntity<JsonNode> response = pdlForvalterRestConsumer.deleteIdent(norskIdent.getIdent());
            appendOkStatus(response.getBody(), status);

        } catch (RuntimeException e) {

            appendErrorStatus(e, status);
            log.error(e.getMessage(), e);
        }
    }

    private static void appendName(String utenlandsIdentifikasjonsnummer, StringBuilder builder) {
        builder.append('$')
                .append(utenlandsIdentifikasjonsnummer);
    }

    private static void appendOkStatus(JsonNode jsonNode, StringBuilder builder) {
        builder.append("&OK");
        if (nonNull(jsonNode) && nonNull(jsonNode.get(HENDELSE_ID))) {
            builder.append(", ")
                    .append(HENDELSE_ID)
                    .append(": ")
                    .append(jsonNode.get(HENDELSE_ID));
        }
    }

    private static void appendErrorStatus(Exception exception, StringBuilder builder) {

        builder.append("&Feil (")
                .append(exception.getMessage());

        if (exception instanceof HttpClientErrorException) {
            String responseBody = ((HttpClientErrorException) exception).getResponseBodyAsString();
            if (responseBody.contains("message")) {
                builder.append(" - message: ")
                        .append(responseBody.substring(responseBody.indexOf("message") + 9, responseBody.indexOf("path") - 2));
            }
        }
        builder.append(')');
    }
}