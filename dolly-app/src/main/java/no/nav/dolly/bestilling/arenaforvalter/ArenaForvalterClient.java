package no.nav.dolly.bestilling.arenaforvalter;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArenaForvalterClient implements ClientRegister {

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (bestilling.getArenaforvalter() == null) {
            progress.setArenaforvalterStatus(null);
            return;
        }

        StringBuilder status = new StringBuilder();
        List<String> environments = new ArrayList<>(arenaForvalterConsumer.getEnvironments());
        environments.retainAll(bestilling.getEnvironments());

        if (!environments.isEmpty()) {

            ResponseEntity<ArenaArbeidssokerBruker> existingServicebruker = fetchServiceBruker(norskIdent.getIdent(), environments, status);
            deleteServicebruker(norskIdent.getIdent(), environments, status, existingServicebruker);

            ArenaNyeBrukere arenaNyeBrukere = new ArenaNyeBrukere();
            environments.forEach(environment -> {
                ArenaNyBruker arenaNyBruker = mapperFacade.map(bestilling.getArenaforvalter(), ArenaNyBruker.class);
                arenaNyBruker.setPersonident(norskIdent.getIdent());
                arenaNyBruker.setMiljoe(environment);
                arenaNyeBrukere.getNyeBrukere().add(arenaNyBruker);
            });

            sendArenadata(arenaNyeBrukere, status);
        }

        List<String> notSupportedEnvironments = new ArrayList<>(bestilling.getEnvironments());
        notSupportedEnvironments.removeAll(environments);
        notSupportedEnvironments.forEach(environment ->
                status.append(',')
                        .append(environment)
                        .append("$Feil: Miljø ikke støttet"));

        progress.setArenaforvalterStatus(status.substring(1));
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(ident -> {
            ResponseEntity<ArenaArbeidssokerBruker> existingServicebruker = arenaForvalterConsumer.getIdent(ident);
            if (existingServicebruker.hasBody()) {
                existingServicebruker.getBody().getArbeidsokerList().forEach(list -> {
                    if (nonNull(list.getMiljoe())) {
                        asList(list.getMiljoe().split(",")).forEach(
                                environment -> arenaForvalterConsumer.deleteIdent(ident, environment));
                    }
                });
            }
        });
    }

    private void deleteServicebruker(String ident, List<String> availEnvironments, StringBuilder status, ResponseEntity<ArenaArbeidssokerBruker> response) {

        if (response.hasBody() && !requireNonNull(response.getBody()).getArbeidsokerList().isEmpty()) {
            response.getBody().getArbeidsokerList().forEach(arbeidssoker -> {
                if (availEnvironments.contains(arbeidssoker.getMiljoe())) {
                    try {
                        arenaForvalterConsumer.deleteIdent(ident, arbeidssoker.getMiljoe());

                    } catch (RuntimeException e) {
                        status.append(',')
                                .append(arbeidssoker.getMiljoe())
                                .append('$');
                        appendErrorText(status, e);
                        log.error("Feilet å inaktivere bruker: {}, miljø: {} i ArenaForvalter: ", arbeidssoker.getPersonident(), arbeidssoker.getMiljoe(), e);
                    }
                }
            });
        }
    }

    private ResponseEntity<ArenaArbeidssokerBruker> fetchServiceBruker(String ident, List<String> availEnvironments, StringBuilder status) {

        try {
            return arenaForvalterConsumer.getIdent(ident);

        } catch (RuntimeException e) {
            availEnvironments.forEach(environment -> {
                status.append(',')
                        .append(environment)
                        .append('$');
                appendErrorText(status, e);
            });
            log.error("Feilet å hente bruker: {} i ArenaForvalter", ident, e);
            return ResponseEntity.badRequest().build();
        }
    }

    private void sendArenadata(ArenaNyeBrukere arenaNyeBrukere, StringBuilder status) {

        try {
            ResponseEntity<ArenaArbeidssokerBruker> response = arenaForvalterConsumer.postArenadata(arenaNyeBrukere);
            if (response.hasBody()) {
                requireNonNull(response.getBody()).getArbeidsokerList().forEach(arbeidsoker ->
                        status.append(',')
                                .append(arbeidsoker.getMiljoe())
                                .append('$')
                                .append(arbeidsoker.getStatus()));
            }

        } catch (RuntimeException e) {

            arenaNyeBrukere.getNyeBrukere().forEach(bruker -> {
                status.append(',')
                        .append(bruker.getMiljoe())
                        .append('$');
                appendErrorText(status, e);
            });
            log.error("Feilet å legge inn ny bruker i ArenaForvalter: ", e);
        }
    }

    private static void appendErrorText(StringBuilder status, RuntimeException e) {
        status.append("Feil: ")
                .append(e.getMessage());

        if (e instanceof HttpClientErrorException) {
            status.append(" (")
                    .append(((HttpClientErrorException) e).getResponseBodyAsString().replace(',', '='))
                    .append(')');
        }
    }
}
