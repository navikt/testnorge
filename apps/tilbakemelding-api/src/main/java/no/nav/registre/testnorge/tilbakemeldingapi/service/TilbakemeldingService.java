package no.nav.registre.testnorge.tilbakemeldingapi.service;

import no.nav.registre.testnorge.tilbakemeldingapi.consumer.ProfilApiConsumer;
import no.nav.registre.testnorge.tilbakemeldingapi.domain.Tilbakemelding;
import no.nav.testnav.libs.slack.consumer.SlackConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        String visningsNavn = Boolean.TRUE.equals(tilbakemelding.getIsAnonym())
                ? "Anonym"
                : profilApiConsumer.getBruker().getVisningsNavn();
        slackConsumer.publish(tilbakemelding.toSlackMessage(channel, visningsNavn));
    }

}
