package no.nav.dolly.bestilling.pdlforvalter;

import static java.time.LocalDate.now;
import static java.util.Objects.nonNull;
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
import no.nav.dolly.domain.resultset.pdlforvalter.folkeregister.PdlFolkeregisterIdent;

@Slf4j
@Service
public class PdlForvalterClient implements ClientRegister {

    private static final String DELETE_IDENT = "DeleteIdent";
    private static final String FOLKEREGISTER_IDENT = "FolkeregisterIdent";
    private static final String KONTAKTINFORMASJON_DOEDSBO = "KontaktinformasjonForDoedsbo";
    private static final String UTENLANDS_IDENTIFIKASJONSNUMMER = "UtenlandskIdentifikasjonsnummer";
    private static final String KILDE = "Dolly";
    private static final String IBRUK = "iBruk";
    private static final String SYNTH_ENV = "q2";
    private static final String HENDELSE_ID = "hendelseId";

    @Autowired
    private PdlForvalterRestConsumer pdlForvalterRestConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    @Override public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        StringBuilder status = new StringBuilder();

        if (bestilling.getEnvironments().contains(SYNTH_ENV)) {

            Pdldata pdldata = mapperFacade.map(bestilling.getPdlforvalter(), Pdldata.class);

            sendDeleteIdent(norskIdent, status);
            sendFolkeregisterIdent(norskIdent, status);
            sendUtenlandsid(pdldata, norskIdent, status);
            sendDoedsbo(pdldata, norskIdent, status);

        } else if (nonNull(bestilling.getPdlforvalter())) {

            status.append("$Info: Bestilling ble ikke utført til ArenaForvalter da miljø '")
                    .append(SYNTH_ENV)
                    .append("' ikke er valgt");
        }

        progress.setPdlforvalterStatus(status.substring(1));
    }

    private void sendUtenlandsid(Pdldata pdldata, NorskIdent norskIdent, StringBuilder status) {

        if (nonNull(pdldata) && nonNull(pdldata.getUtenlandskIdentifikasjonsnummer())) {
            try {
                appendName(UTENLANDS_IDENTIFIKASJONSNUMMER, status);

                pdldata.getUtenlandskIdentifikasjonsnummer().setKilde(KILDE);
                pdldata.getUtenlandskIdentifikasjonsnummer().setGyldigFom(
                        nullcheckSetDefaultValue(pdldata.getUtenlandskIdentifikasjonsnummer().getGyldigFom(), now()));
                ResponseEntity<JsonNode> response =
                        pdlForvalterRestConsumer.postUtenlandskIdentifikasjonsnummer(pdldata.getUtenlandskIdentifikasjonsnummer(), norskIdent.getIdent());

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
                kontaktinformasjon.setUtstedtDato(
                        nullcheckSetDefaultValue(kontaktinformasjon.getUtstedtDato(), now()));
                ResponseEntity<JsonNode> response =
                        pdlForvalterRestConsumer.postKontaktinformasjonForDoedsbo(kontaktinformasjon, norskIdent.getIdent());

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

    private void sendFolkeregisterIdent(NorskIdent norskIdent, StringBuilder status) {

        try {
            appendName(FOLKEREGISTER_IDENT, status);

            ResponseEntity<JsonNode> response = pdlForvalterRestConsumer.postFolkeregisterIdent(PdlFolkeregisterIdent.builder()
                    .idnummer(norskIdent.getIdent())
                    .gyldigFom(now())
                    .type(norskIdent.getIdentType())
                    .status(IBRUK)
                    .kilde(KILDE)
                    .build());

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
        builder.append("&status: OK");
        if (nonNull(jsonNode) && nonNull(jsonNode.get(HENDELSE_ID))) {
            builder.append(", ")
                    .append(HENDELSE_ID)
                    .append(": ")
                    .append(jsonNode.get(HENDELSE_ID));
        }
    }

    private static void appendErrorStatus(Exception exception, StringBuilder builder) {

        builder.append("&status: Feil (")
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