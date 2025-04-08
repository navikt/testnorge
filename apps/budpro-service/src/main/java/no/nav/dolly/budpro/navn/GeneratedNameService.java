package no.nav.dolly.budpro.navn;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.texas.Texas;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneratedNameService {

    private final Texas texas;

    @PostConstruct
    void postConstruct() {
        log.info("Getting token...");
        var token = texas
                .getToken("generer-navn-service")
                .block();
        if (token != null) {
            log.info("Got a token with TTL of {} seconds", token.expires_in());
        } else {
            log.error("Got a null token");
        }
    }

    public String[] getNames(Long seed, int number) {
        var names = new GenererNavnCommand(texas, seed, number).call();
        return Arrays
                .stream(names)
                .map(name -> name.getAdjektiv() + " " + name.getSubstantiv())
                .toList()
                .toArray(new String[0]);
    }

}
