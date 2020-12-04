package no.nav.registre.testnorge.tilbakemeldingapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.libs.slack.consumer.SlackConsumer;
import no.nav.registre.testnorge.tilbakemeldingapi.consumer.ProfilApiConsumer;
import no.nav.registre.testnorge.tilbakemeldingapi.domain.Tilbakemelding;

@Service
public class TilbakemeldingService {
    private final SlackConsumer slackConsumer;
    private final ProfilApiConsumer profilApiConsumer;
    private final String channel;

    public TilbakemeldingService(
            SlackConsumer slackConsumer,
            ProfilApiConsumer profilApiConsumer,
            @Value("${consumer.slack.channel}") String channel
    ) {
        this.slackConsumer = slackConsumer;
        this.profilApiConsumer = profilApiConsumer;
        this.channel = channel;
    }

    public void publish(Tilbakemelding tilbakemelding) {
        String visningsNavn = tilbakemelding.getIsAnonym()
                ? "Anonym"
                : profilApiConsumer.getBruker().getVisningsNavn();
        slackConsumer.publish(tilbakemelding.toSlackMessage(channel, visningsNavn));
    }

}
