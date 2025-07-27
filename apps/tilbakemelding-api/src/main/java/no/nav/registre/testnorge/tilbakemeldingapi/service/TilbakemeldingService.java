package no.nav.registre.testnorge.tilbakemeldingapi.service;

import no.nav.registre.testnorge.tilbakemeldingapi.consumer.ProfilApiConsumer;
import no.nav.registre.testnorge.tilbakemeldingapi.domain.Tilbakemelding;
import no.nav.testnav.libs.slack.consumer.SlackConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;

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
                : utledVisningsNavn(tilbakemelding);
        slackConsumer.publish(tilbakemelding.toSlackMessage(channel, visningsNavn));
    }

    public String utledVisningsNavn(Tilbakemelding tilbakemelding) {
        return isNoneBlank(tilbakemelding.getBrukernavn()) ?
                String.format("%s (%s)", tilbakemelding.getBrukernavn(),
                        isNoneBlank(tilbakemelding.getTilknyttetOrganisasjon()) ? tilbakemelding.getTilknyttetOrganisasjon() : "Ukjent organisasjon") :
                profilApiConsumer.getBruker().getVisningsNavn();
    }

}
