package no.nav.dolly.synt.meldekort.generator;

import java.util.List;

record GeneratedMeldekort(
        Object arbeidssoker,
        Object arbeidet,
        Object syk,
        Object annetFravaer,
        Object kurs,
        Object forskudd,
        String signatur,
        List<GeneratedMeldekortDag> meldekortDager
) {
}
