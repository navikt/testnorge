package no.nav.dolly.synt.meldekort.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MeldekortService {

    private final MeldekortGenerator generator;

    public List<String> generateMeldekort(MeldekortType meldegruppe, int antallMeldekort, Double arbeidstimerOverride) {
        return generator.generateMeldekort(meldegruppe, antallMeldekort, arbeidstimerOverride, ThreadLocalRandom.current());
    }

}
