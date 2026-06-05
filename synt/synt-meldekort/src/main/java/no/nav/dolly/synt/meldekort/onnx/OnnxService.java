package no.nav.dolly.synt.meldekort.onnx;

import java.util.List;

public interface OnnxService {

    List<String> generateMeldekort(MeldekortType meldegruppe, int antallMeldekort, Double arbeidstimerOverride);

}

