package no.nav.dolly.synt.meldekort.onnx;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.meldekort.SyntMeldekortApplication;
import no.nav.dolly.synt.meldekort.models.BucketModels;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("prod")
@Slf4j
@RequiredArgsConstructor
class OnnxBucketService implements OnnxService {

    private final SyntMeldekortApplication.Config config;
    private MeldekortGenerator generator;

    @PostConstruct
    void postConstruct()
            throws Exception {

        var modelDirectory = BucketModels.get(config.getBucket(), config.getModels(), "synt-meldekort-models-");
        this.generator = new MeldekortGenerator(modelDirectory);
        log.info("Successfully initialized ONNX models from bucket {}", config.getBucket());

    }

    @Override
    public List<String> generateMeldekort(MeldekortType meldegruppe, int antallMeldekort, Double arbeidstimerOverride) {
        return generator.generateMeldekort(meldegruppe, antallMeldekort, arbeidstimerOverride);
    }

}

