package no.nav.dolly.synt.meldekort.generator;

import java.util.List;

public interface MeldekortService {

    List<String> generateMeldekort(MeldekortType meldegruppe, int antallMeldekort, Double arbeidstimerOverride);

}
