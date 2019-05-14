package no.nav.dolly.bestilling.pdlforvalter;

import static java.time.LocalDate.now;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.pdlforvalter.Pdldata;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.folkeregister.PdlFolkeregisterIdent;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;

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

    @Autowired
    private PdlForvalterRestConsumer pdlForvalterRestConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    @Override public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (bestilling.getEnvironments().contains(SYNTH_ENV)) {

            Pdldata pdldata = mapperFacade.map(bestilling.getPdlforvalter(), Pdldata.class);

            StringBuilder status = new StringBuilder();
            sendDeleteIdent(norskIdent, status);
            sendFolkeregisterIdent(norskIdent, status);
            sendUtenlandsid(pdldata.getUtenlandskIdentifikasjonsnummer(), norskIdent, status);
            sendDoedsbo(pdldata.getKontaktinformasjonForDoedsbo(), norskIdent, status);

            progress.setPdlforvalterStatus(status.substring(1));
        }
    }

    private void sendUtenlandsid(PdlUtenlandskIdentifikasjonsnummer utenlandskIdentifikasjonsnummer, NorskIdent norskIdent, StringBuilder status) {

        if (nonNull(utenlandskIdentifikasjonsnummer)) {
            try {
                appendName(UTENLANDS_IDENTIFIKASJONSNUMMER, status);

                utenlandskIdentifikasjonsnummer.setKilde(KILDE);
                utenlandskIdentifikasjonsnummer.setGyldigFom(
                        nullcheckSetDefaultValue(utenlandskIdentifikasjonsnummer.getGyldigFom(), now()));
                ResponseEntity<String> response =
                        pdlForvalterRestConsumer.postUtenlandskIdentifikasjonsnummer(utenlandskIdentifikasjonsnummer, norskIdent.getIdent());

                appendOkStatus(response.getBody(), status);

            } catch (Exception exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendDoedsbo(PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo, NorskIdent norskIdent, StringBuilder status) {

        if (nonNull(kontaktinformasjonForDoedsbo)) {
            try {
                appendName(KONTAKTINFORMASJON_DOEDSBO, status);

                kontaktinformasjonForDoedsbo.setKilde(KILDE);
                kontaktinformasjonForDoedsbo.setUtstedtDato(nullcheckSetDefaultValue(kontaktinformasjonForDoedsbo.getUtstedtDato(), now()));
                ResponseEntity<String> response =
                        pdlForvalterRestConsumer.postKontaktinformasjonForDoedsbo(kontaktinformasjonForDoedsbo, norskIdent.getIdent());

                appendOkStatus(response.getBody(), status);

            } catch (Exception exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendDeleteIdent(NorskIdent norskIdent, StringBuilder status) {

        try {
            appendName(DELETE_IDENT, status);
            ResponseEntity<String> response = pdlForvalterRestConsumer.deleteIdent(norskIdent.getIdent());
            appendOkStatus(response.getBody(), status);

        } catch (Exception e) {

            appendErrorStatus(e, status);
            log.error(e.getMessage(), e);
        }
    }

    private void sendFolkeregisterIdent(NorskIdent norskIdent, StringBuilder status) {

        try {
            appendName(FOLKEREGISTER_IDENT, status);

            ResponseEntity<String> response = pdlForvalterRestConsumer.postFolkeregisterIdent(PdlFolkeregisterIdent.builder()
                    .ident(norskIdent.getIdent())
                    .idnummer(norskIdent.getIdent())
                    .gyldigFom(now())
                    .type(norskIdent.getIdentType())
                    .status(IBRUK)
                    .kilde(KILDE)
                    .build());

            appendOkStatus(response.getBody(), status);

        } catch (Exception e) {

            appendErrorStatus(e, status);
            log.error(e.getMessage(), e);
        }
    }

    private static void appendName(String utenlandsIdentifikasjonsnummer, StringBuilder builder) {
        builder.append('$')
                .append(utenlandsIdentifikasjonsnummer);
    }

    private static void appendOkStatus(String body, StringBuilder builder) {
        builder.append("&status: OK, hendelsesId: ")
                .append(trimHendelseId(body));
    }

    private static void appendErrorStatus(Exception exception, StringBuilder builder) {

        builder.append("&status: Feil (")
                .append(exception.getMessage());

        if (exception instanceof HttpClientErrorException) {
            String responseBody = ((HttpClientErrorException) exception).getResponseBodyAsString();
            if (responseBody.indexOf("message") != -1) {
                builder.append(" - message: ")
                        .append(responseBody.substring(responseBody.indexOf("message") + 9, responseBody.indexOf("path") - 2));
            }
        }
        builder.append(')');
    }

    private static String trimHendelseId(String jsonNode) {
        return nonNull(jsonNode) && jsonNode.substring(jsonNode.indexOf(':')).length() > 3 ?
                jsonNode.substring(jsonNode.indexOf(':') + 2, jsonNode.length() - 2) : "";
    }
}