package no.nav.dolly.budpro.navn;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.texas.Texas;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GeneratedNameService {

    private final Texas texas;

    public Flux<String> getNames(Long seed, int number) {
        return new GenererNavnCommand(texas, seed, number)
                .call()
                .map(name -> name.getAdjektiv() + " " + name.getSubstantiv());
    }

}
