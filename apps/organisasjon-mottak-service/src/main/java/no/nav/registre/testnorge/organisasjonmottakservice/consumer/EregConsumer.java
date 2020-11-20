package no.nav.registre.testnorge.organisasjonmottakservice.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.organisasjonmottakservice.domain.Flatfil;

@RequiredArgsConstructor
public class EregConsumer {
    private final JenkinsConsumer jenkinsConsumer;

    public void save(Flatfil flatfil, String miljo) {
        jenkinsConsumer.send(flatfil, miljo);
    }
}
