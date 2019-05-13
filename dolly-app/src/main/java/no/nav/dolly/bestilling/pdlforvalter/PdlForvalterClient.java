package no.nav.dolly.bestilling.pdlforvalter;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import java.time.LocalDate;
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

    private static final String FOLKEREGISTER_IDENT = "FolkeregisterIdent";
    private static final String KONTAKTINFORMASJON_DOEDSBO = "KontaktinformasjonForDoedsbo";
    private static final String UTENLANDS_IDENTIFIKASJONSNUMMER = "UtenlandskIdentifikasjonsnummer";
    private static final String KILDE = "Dolly";
    private static final String IBRUK = "iBruk";

    @Autowired
    private PdlForvalterRestConsumer pdlForvalterRestConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    @Override public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        StringBuilder status = new StringBuilder();
        Pdldata pdldata = mapperFacade.map(bestilling.getPdlforvalter(), Pdldata.class);

        sendFolkeregisterIdent(norskIdent, status);
        sendUtenlandsid(pdldata.getUtenlandskIdentifikasjonsnummer(), norskIdent.getIdent(), status);
        sendDoedsbo(pdldata.getKontaktinformasjonForDoedsbo(), norskIdent.getIdent(), status);

        progress.setPdlforvalterStatus(status.substring(1));
    }

    private void sendUtenlandsid(PdlUtenlandskIdentifikasjonsnummer utenlandskIdentifikasjonsnummer, String ident, StringBuilder status) {

        if (nonNull(utenlandskIdentifikasjonsnummer)) {
            try {
                utenlandskIdentifikasjonsnummer.setKilde(KILDE);
                utenlandskIdentifikasjonsnummer.setIdent(ident);
                utenlandskIdentifikasjonsnummer.setGyldigFom(nullcheckSetDefaultValue(
                        utenlandskIdentifikasjonsnummer.getGyldigFom(), LocalDate.now()
                ));

                appendName(UTENLANDS_IDENTIFIKASJONSNUMMER, status);
                ResponseEntity<String> response = pdlForvalterRestConsumer.postUtenlandskIdentifikasjonsnummer(utenlandskIdentifikasjonsnummer);

                appendOkStatus(response.getBody(), status);

            } catch (Exception exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendDoedsbo(PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo, String ident, StringBuilder status) {

        if (nonNull(kontaktinformasjonForDoedsbo)) {
            try {
                kontaktinformasjonForDoedsbo.setKilde(KILDE);
                kontaktinformasjonForDoedsbo.setIdent(ident);
                kontaktinformasjonForDoedsbo.setUtstedtDato(nullcheckSetDefaultValue(kontaktinformasjonForDoedsbo.getUtstedtDato(), LocalDate.now()));

                appendName(KONTAKTINFORMASJON_DOEDSBO, status);
                ResponseEntity<String> response = pdlForvalterRestConsumer.postKontaktinformasjonForDoedsbo(kontaktinformasjonForDoedsbo);

                appendOkStatus(response.getBody(), status);

            } catch (Exception exception) {

                appendErrorStatus(exception, status);
                log.error(exception.getMessage(), exception);
            }
        }
    }

    private void sendFolkeregisterIdent(NorskIdent norskIdent, StringBuilder status) {

        try {

            appendName(FOLKEREGISTER_IDENT, status);

            ResponseEntity<String> response = pdlForvalterRestConsumer.postFolkeregisterIdent(PdlFolkeregisterIdent.builder()
                    .ident(norskIdent.getIdent())
                    .idnummer(norskIdent.getIdent())
                    .gyldigFom(LocalDate.now())
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
        builder.append("&status: OK, hendelsesId:")
                .append(trimHendelseId(body));
    }

    private static void appendErrorStatus(Exception exception, StringBuilder builder) {

        builder.append("&status: Feil (")
                .append(exception.getMessage());

        if (exception instanceof HttpClientErrorException) {
            String responseBody = ((HttpClientErrorException) exception).getResponseBodyAsString();
            builder.append(" - message: ")
                    .append(responseBody.substring(responseBody.indexOf("message") + 9, responseBody.indexOf("path") - 2));
        }
        builder.append(')');
    }

    private static String trimHendelseId(String jsonNode) {
        return jsonNode.substring(jsonNode.indexOf(':') + 2, jsonNode.length() - 2);
    }
}