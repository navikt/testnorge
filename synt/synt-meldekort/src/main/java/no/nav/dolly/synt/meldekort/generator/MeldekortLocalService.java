package no.nav.dolly.synt.meldekort.generator;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.meldekort.models.LocalModels;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Profile("local")
@Slf4j
class MeldekortLocalService implements MeldekortService {

    private MeldekortGenerator generator;

    @PostConstruct
    void postConstruct() {

        var modelDirectory = LocalModels.get();
        this.generator = MeldekortGenerator.fromDirectory(modelDirectory);
        log.info("Successfully initialized meldekort models from folder {}", modelDirectory.toAbsolutePath());

    }

    @Override
    public List<String> generateMeldekort(MeldekortType meldegruppe, int antallMeldekort, Double arbeidstimerOverride) {
        return generator.generateMeldekort(meldegruppe, antallMeldekort, arbeidstimerOverride, ThreadLocalRandom.current());
    }

}
