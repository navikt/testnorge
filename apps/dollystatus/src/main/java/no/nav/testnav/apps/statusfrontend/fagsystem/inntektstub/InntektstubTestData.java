package no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub;

import java.util.List;

final class InntektstubTestData {

    static final String RESERVED_YEAR_MONTH = "2099-12";
    static final String RESERVED_INCOME_TYPE = "LOENNSINNTEKT";

    private InntektstubTestData() {
    }

    static InntektstubRequest request(String ident) {
        return new InntektstubRequest(
                ident,
                RESERVED_YEAR_MONTH,
                "991825827",
                "991825827",
                List.of(new InntektstubRequest.Income(
                        RESERVED_INCOME_TYPE,
                        1234.0,
                        "fastloenn",
                        true,
                        true)));
    }
}
