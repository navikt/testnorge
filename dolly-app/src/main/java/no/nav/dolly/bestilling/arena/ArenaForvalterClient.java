package no.nav.dolly.bestilling.arena;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukereMedServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukereUtenServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaBrukerMedServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaBrukerUtenServicebehov;

@Slf4j
@Service
public class ArenaForvalterClient implements ClientRegister {

    private static final String ARENA_FORVALTER_ENV = "q2";

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @Override public void gjenopprett(RsDollyBestilling bestilling, String ident, BestillingProgress progress) {

        if (nonNull(bestilling.getArenaForvalter())) {

            StringBuilder status = new StringBuilder();

            if (bestilling.getEnvironments().contains(ARENA_FORVALTER_ENV)) {

                deleteArenadata(ident, status);

                if (bestilling.getArenaForvalter() instanceof RsArenaBrukerUtenServicebehov) {

                    ((RsArenaBrukerUtenServicebehov) bestilling.getArenaForvalter()).setPersonident(ident);
                    sendArenadata(ArenaBrukereUtenServicebehov.builder()
                            .nyeBrukereUtenServiceBehov(singletonList(bestilling.getArenaForvalter()))
                            .build(), status);
                } else {

                    ((RsArenaBrukerMedServicebehov) bestilling.getArenaForvalter()).setPersonident(ident);
                    sendArenadata(ArenaBrukereMedServicebehov.builder()
                            .nyeBrukere(singletonList(bestilling.getArenaForvalter()))
                            .build(), status);
                }

            } else {

                status.append(format("$Info: Brukere ikke opprettet i ArenaForvalter da miljø '%s' ikke er valgt", ARENA_FORVALTER_ENV));
            }
            progress.setArenastubStatus(status.substring(1));
        }
    }

    private void deleteArenadata(String ident, StringBuilder status) {
        try {
            status.append("$arenaDeleteBruker&status: ");

            arenaForvalterConsumer.deleteIdent(ident);
            status.append("OK");

        } catch (RuntimeException e) {

            appendErrorText(status, e);
            log.error("Feilet å slette ident i ArenaForvalter: ", e);
        }
    }

    private void sendArenadata(Arenadata arenadata, StringBuilder status) {

        try {
            status.append("$arenaOpprettBruker&status: ");

            arenaForvalterConsumer.postArenadata(arenadata);
            status.append("OK");

        } catch (RuntimeException e) {

            appendErrorText(status, e);
            log.error("Feilet å legge inn bruker i ArenaForvalter: ", e);
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
