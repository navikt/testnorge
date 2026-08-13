package no.nav.dolly.synt.meldekort.onnx;

import java.util.List;

record GeneratedMeldekort(
        boolean arbeidssoker,
        boolean arbeidet,
        boolean syk,
        boolean annetFravaer,
        boolean kurs,
        boolean forskudd,
        String signatur,
        List<GeneratedMeldekortDag> meldekortDager
) {
}

