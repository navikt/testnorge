package no.nav.testnav.apps.statusfrontend.functionaltest.exception;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;

public class FunctionalTestRunInProgressException extends RuntimeException {

    private final RunId runId;

    public FunctionalTestRunInProgressException(RunId runId) {
        super("En funksjonstestkjøring pågår.");
        this.runId = runId;
    }

    public RunId getRunId() {
        return runId;
    }
}
