package no.nav.dolly.synt.meldekort.onnx;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("test")
class OnnxTestService implements OnnxService {

    @Override
    public List<String> generateMeldekort(MeldekortType meldegruppe, int antallMeldekort, Double arbeidstimerOverride) {
        return List.of();
    }

}

