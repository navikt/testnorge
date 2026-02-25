package no.nav.registre.testnorge.tilbakemeldingapi.service;

import no.nav.registre.testnorge.tilbakemeldingapi.consumer.ProfilApiConsumer;
import no.nav.registre.testnorge.tilbakemeldingapi.domain.Tilbakemelding;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
import no.nav.testnav.libs.slack.consumer.SlackConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    public Mono<Void> publish(Tilbakemelding tilbakemelding) {
        return utledVisningsNavn(tilbakemelding)
                .map(visningsNavn -> {
                    slackConsumer.publish(tilbakemelding.toSlackMessage(channel, visningsNavn));
                    return visningsNavn;
                })
                .then();
    }

    private Mono<String> utledVisningsNavn(Tilbakemelding tilbakemelding) {
        if (Boolean.TRUE.equals(tilbakemelding.getIsAnonym())) {
            return Mono.just("Anonym");
        }
        if (isNoneBlank(tilbakemelding.getBrukernavn())) {
            return Mono.just(String.format("%s (%s)", tilbakemelding.getBrukernavn(),
                    isNoneBlank(tilbakemelding.getTilknyttetOrganisasjon()) ? tilbakemelding.getTilknyttetOrganisasjon() : "Ukjent organisasjon"));
        }
        return profilApiConsumer.getBruker()
                .map(ProfilDTO::getVisningsNavn);
    }

}
