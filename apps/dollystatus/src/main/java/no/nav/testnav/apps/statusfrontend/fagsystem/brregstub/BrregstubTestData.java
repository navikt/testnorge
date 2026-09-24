package no.nav.testnav.apps.statusfrontend.fagsystem.brregstub;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;

import java.time.ZoneOffset;
import java.util.List;

final class BrregstubTestData {

    static final int ORGANIZATION_NUMBER = 991825827;

    private BrregstubTestData() {
    }

    static BrregstubRequest request(String ident, FunctionalTestContext context) {
        var registrationDate = context.startedAt().atZone(ZoneOffset.UTC).toLocalDate();
        var name = new BrregstubRequest.Name("Dollystatus", null, "Testperson");
        var address = new BrregstubRequest.Address(
                "Testveien 1",
                null,
                null,
                "0555",
                "Oslo",
                "NO",
                "0301");
        var role = new BrregstubRequest.Role(
                registrationDate,
                "DELT",
                "Deltakere",
                ORGANIZATION_NUMBER,
                new BrregstubRequest.Name("Dollystatus testorganisasjon", null, null),
                address,
                address,
                List.of(new BrregstubRequest.RoleStatus("DELTAGER", false)));
        return new BrregstubRequest(
                ident,
                registrationDate.minusYears(30),
                name,
                address,
                List.of(role),
                0,
                List.of());
    }
}
