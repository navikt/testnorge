package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunStatus;

public interface FunctionalTestResultListener {

    void onCompleted(FunctionalTestStatus status);

    default void onFullRunCompleted(FunctionalTestRunStatus run) {
    }
}
