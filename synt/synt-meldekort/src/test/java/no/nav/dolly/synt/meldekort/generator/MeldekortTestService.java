package no.nav.dolly.synt.meldekort.generator;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("test")
class MeldekortTestService implements MeldekortService {

    @Override
    public List<String> generateMeldekort(MeldekortType meldegruppe, int antallMeldekort, Double arbeidstimerOverride) {
        return List.of();
    }

}

