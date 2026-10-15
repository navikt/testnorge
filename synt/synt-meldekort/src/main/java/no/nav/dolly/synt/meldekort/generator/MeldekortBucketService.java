package no.nav.dolly.synt.meldekort.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.meldekort.SyntMeldekortApplication;
import no.nav.dolly.synt.meldekort.models.BucketModels;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Profile("prod")
@Slf4j
@RequiredArgsConstructor
class MeldekortBucketService implements MeldekortService {

    private final SyntMeldekortApplication.Config config;
    private MeldekortGenerator generator;

    @PostConstruct
    void postConstruct()
            throws Exception {

        var modelDirectory = BucketModels.get(config.getBucket(), config.getModels(), "synt-meldekort-models-");
        this.generator = MeldekortGenerator.fromDirectory(modelDirectory);
        log.info("Successfully initialized meldekort models from bucket {}", config.getBucket());

    }

    @Override
    public List<String> generateMeldekort(MeldekortType meldegruppe, int antallMeldekort, Double arbeidstimerOverride) {
        return generator.generateMeldekort(meldegruppe, antallMeldekort, arbeidstimerOverride, ThreadLocalRandom.current());
    }

}
