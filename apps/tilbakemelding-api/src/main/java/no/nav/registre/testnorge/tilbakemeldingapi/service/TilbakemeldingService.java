package no.nav.registre.testnorge.tilbakemeldingapi.service;

import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.tilbakemeldingapi.consumer.ProfilApiConsumer;
import no.nav.registre.testnorge.tilbakemeldingapi.domain.Tilbakemelding;
import no.nav.testnav.libs.slack.consumer.SlackConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TilbakemeldingService {
    private final SlackConsumer slackConsumer;
    private final ProfilApiConsumer profilApiConsumer;
    private final String channel;

    public TilbakemeldingService(
            SlackConsumer slackConsumer,
            ProfilApiConsumer profilApiConsumer,
            @Value("${consumers.slack.channel}") String channel
    ) {
        this.slackConsumer = slackConsumer;
        this.profilApiConsumer = profilApiConsumer;
        this.channel = channel;
    }

    public void publish(Tilbakemelding tilbakemelding) {
        if (Boolean.FALSE.equals(tilbakemelding.getIsAnonym())) {
            log.info("Bruker {} har sendt tilbakemelding", Json.pretty(profilApiConsumer.getBruker()));
        }
        String visningsNavn = Boolean.TRUE.equals(tilbakemelding.getIsAnonym())
                ? "Anonym"
                : profilApiConsumer.getBruker().getVisningsNavn();
        slackConsumer.publish(tilbakemelding.toSlackMessage(channel, visningsNavn));
    }

}
