package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;

@Component
@RequiredArgsConstructor
public class EregConsumer {
    private final JenkinsConsumer jenkinsConsumer;

    public void save(Flatfil flatfil, String miljo) {
        jenkinsConsumer.send(flatfil, miljo);
    }
}
