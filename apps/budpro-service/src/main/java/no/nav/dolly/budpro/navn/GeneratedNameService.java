package no.nav.dolly.budpro.navn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.texas.Texas;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneratedNameService {

    private final WebClient webClient;
    private final Texas texas;

    public String[] getNames(Long seed, int number) {

        var token = texas.getToken("generer-navn-service");
        var names = new GenererNavnCommand(webClient, token, seed, number).call();
        return Arrays
                .stream(names)
                .map(name -> name.getAdjektiv() + " " + name.getSubstantiv())
                .toList()
                .toArray(new String[0]);

    }

}
