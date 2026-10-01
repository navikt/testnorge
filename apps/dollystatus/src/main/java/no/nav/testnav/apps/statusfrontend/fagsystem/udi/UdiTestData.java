package no.nav.testnav.apps.statusfrontend.fagsystem.udi;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;

import java.time.ZoneOffset;

@UtilityClass
class UdiTestData {

    static UdiRequest request(String ident, FunctionalTestContext context) {
        var testDate = context.startedAt().atZone(ZoneOffset.UTC).toLocalDate();
        return new UdiRequest(
                ident,
                new UdiRequest.Name("Dollystatus", null, "Testperson"),
                testDate.minusYears(30),
                false,
                true,
                false,
                "NEI",
                testDate);
    }
}
