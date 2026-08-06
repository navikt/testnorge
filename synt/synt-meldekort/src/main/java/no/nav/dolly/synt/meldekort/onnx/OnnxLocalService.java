package no.nav.dolly.synt.meldekort.onnx;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.meldekort.models.LocalModels;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("local")
@Slf4j
class OnnxLocalService implements OnnxService {

    private MeldekortGenerator generator;

    @PostConstruct
    void postConstruct() {

        var modelDirectory = LocalModels.get();
        this.generator = new MeldekortGenerator(modelDirectory);
        log.info("Successfully initialized ONNX models from folder {}", modelDirectory.toAbsolutePath());

    }

    @Override
    public List<String> generateMeldekort(MeldekortType meldegruppe, int antallMeldekort, Double arbeidstimerOverride) {
        return generator.generateMeldekort(meldegruppe, antallMeldekort, arbeidstimerOverride);
    }

}

