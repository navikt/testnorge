package no.nav.testnav.apps.statusfrontend.api.model;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestError;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory;

public record FunctionalTestErrorResponse(FunctionalTestErrorCategory category, String message) {

    static FunctionalTestErrorResponse from(FunctionalTestError error) {
        return error == null ? null : new FunctionalTestErrorResponse(error.category(), error.message());
    }
}
