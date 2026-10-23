package no.nav.dolly.synt.tilleggsstonad.generator;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.tilleggsstonad.SyntTilleggsstonadApplication;
import no.nav.dolly.synt.tilleggsstonad.models.BucketModels;
import no.nav.dolly.synt.tilleggsstonad.models.LocalModels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
class TilleggsstonadGeneratorConfig {

    @Bean
    @Profile("prod")
    TilleggsstonadGenerator bucketTilleggsstonadGenerator(SyntTilleggsstonadApplication.Config config)
            throws Exception {

        var modelDirectory = BucketModels.get(config.getBucket(), config.getModels(), "synt-tilleggsstonad-models-");
        var generator = TilleggsstonadGenerator.fromDirectory(modelDirectory);
        log.info("Successfully initialized tilleggsstonad models from bucket {}", config.getBucket());
        return generator;

    }

    @Bean
    @Profile("local")
    TilleggsstonadGenerator localTilleggsstonadGenerator() {

        var modelDirectory = LocalModels.get();
        var generator = TilleggsstonadGenerator.fromDirectory(modelDirectory);
        log.info("Successfully initialized tilleggsstonad models from folder {}", modelDirectory.toAbsolutePath());
        return generator;

    }

}
