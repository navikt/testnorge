package no.nav.dolly.synt.meldekort.generator;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.meldekort.SyntMeldekortApplication;
import no.nav.dolly.synt.meldekort.models.BucketModels;
import no.nav.dolly.synt.meldekort.models.LocalModels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
class MeldekortGeneratorConfig {

    @Bean
    @Profile("prod")
    MeldekortGenerator bucketMeldekortGenerator(SyntMeldekortApplication.Config config)
            throws Exception {

        var modelDirectory = BucketModels.get(config.getBucket(), config.getModels(), "synt-meldekort-models-");
        var generator = MeldekortGenerator.fromDirectory(modelDirectory);
        log.info("Successfully initialized meldekort models from bucket {}", config.getBucket());
        return generator;

    }

    @Bean
    @Profile("local")
    MeldekortGenerator localMeldekortGenerator() {

        var modelDirectory = LocalModels.get();
        var generator = MeldekortGenerator.fromDirectory(modelDirectory);
        log.info("Successfully initialized meldekort models from folder {}", modelDirectory.toAbsolutePath());
        return generator;

    }

}
