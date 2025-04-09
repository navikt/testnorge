package no.nav.dolly.budpro.navn;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.texas.Texas;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class GeneratedNameService {

    private final Texas texas;

    public String[] getNames(Long seed, int number) {
        var names = new GenererNavnCommand(texas, seed, number).call();
        return Arrays
                .stream(names)
                .map(name -> name.getAdjektiv() + " " + name.getSubstantiv())
                .toList()
                .toArray(new String[0]);
    }

}
