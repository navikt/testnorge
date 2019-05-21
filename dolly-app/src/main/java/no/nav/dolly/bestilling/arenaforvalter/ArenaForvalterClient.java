package no.nav.dolly.bestilling.arenaforvalter;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukertype.UTEN_SERVICEBEHOV;

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
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukerMedServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukerUtenServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukereMedServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukereUtenServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaServicedata;

@Slf4j
@Service
public class ArenaForvalterClient implements ClientRegister {

    private static final String ARENA_FORVALTER_ENV = "q2";

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (nonNull(bestilling.getArenaforvalter())) {

            StringBuilder status = new StringBuilder();

            if (bestilling.getEnvironments().contains(ARENA_FORVALTER_ENV)) {

                bestilling.getArenaforvalter().setPersonident(norskIdent.getIdent());
                checkAndDeleteArenadata(norskIdent.getIdent(), status);

                sendArenadata(UTEN_SERVICEBEHOV.equals(bestilling.getArenaforvalter().getArenaBrukertype()) ?

                                ArenaBrukereUtenServicebehov.builder()
                                        .nyeBrukereUtenServiceBehov(singletonList(
                                                mapperFacade.map(bestilling.getArenaforvalter(), ArenaBrukerUtenServicebehov.class)))
                                        .build() :

                                ArenaBrukereMedServicebehov.builder()
                                        .nyeBrukere(singletonList(
                                                mapperFacade.map(bestilling.getArenaforvalter(), ArenaBrukerMedServicebehov.class)))
                                        .build()

                        , status);

            } else {

                status.append(format("$ArenaForvalter&Status: Feil: Brukere ikke opprettet i ArenaForvalter da miljø '%s' ikke er valgt", ARENA_FORVALTER_ENV));
            }
            progress.setArenaforvalterStatus(status.substring(1));
        }

    }

    private void checkAndDeleteArenadata(String ident, StringBuilder status) {

        try {
            status.append("$HentBruker&status: ");
            ResponseEntity<JsonNode> response = arenaForvalterConsumer.getIdent(ident);
            status.append(response.getStatusCode().getReasonPhrase());

            if (response.hasBody() && response.getBody().get("arbeidsokerList").size() > 0) {
                status.append("$InaktiverBruker&status: ");
                ResponseEntity<JsonNode> deleteResponse = arenaForvalterConsumer.deleteIdent(ident);
                status.append(deleteResponse.getStatusCode().getReasonPhrase());
            }

        } catch (RuntimeException e) {

            status.append("$ArenaForvalter&status: ");
            appendErrorText(status, e);
            log.error("Feilet å hente eller inaktivere bruker i ArenaForvalter: ", e);
        }
    }

    private void sendArenadata(ArenaServicedata arenaServicedata, StringBuilder status) {

        try {
            status.append("$OpprettNyBruker&status: ");

            ResponseEntity<JsonNode> response = arenaForvalterConsumer.postArenadata(arenaServicedata);
            status.append(response.hasBody() ? response.getBody().get("arbeidsokerList").get(0).get("status").asText() : "Ukjent");

        } catch (RuntimeException e) {

            appendErrorText(status, e);
            log.error("Feilet å legge inn ny bruker i ArenaForvalter: ", e);
        }
    }

    private void appendErrorText(StringBuilder status, RuntimeException e) {
        status.append("FEIL: ")
                .append(e.getMessage());

        if (e instanceof HttpClientErrorException) {
            status.append(" (")
                    .append(((HttpClientErrorException) e).getResponseBodyAsString())
                    .append(')');
        }
    }
}
