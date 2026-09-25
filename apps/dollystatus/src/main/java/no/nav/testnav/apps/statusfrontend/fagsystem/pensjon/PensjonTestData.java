package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;

final class PensjonTestData {

    static final String TP_ORDNING = "3010";
    static final int POPP_YEAR = 2020;
    static final int POPP_AMOUNT = 12345;
    static final String AFP_TP_ID = "4099";
    static final String PENSJONSAVTALE_PRODUCT = "Dollystatus syntetisk kontrakttest";
    static final List<String> PEN_ENVIRONMENTS = List.of("q1", "q2");

    private PensjonTestData() {
    }

    static String environmentName(FunctionalTestEnvironment environment) {
        if (environment != FunctionalTestEnvironment.Q1
                && environment != FunctionalTestEnvironment.Q2) {
            throw new IllegalArgumentException("Pensjon-miljøet støttes ikke.");
        }
        return environment.name().toLowerCase(Locale.ROOT);
    }

    static TpForholdRequest tpRequest(String ident, FunctionalTestEnvironment environment) {
        return new TpForholdRequest(Set.of(environmentName(environment)), ident, TP_ORDNING);
    }

    static PoppInntektRequest poppRequest(String ident, FunctionalTestEnvironment environment) {
        return new PoppInntektRequest(
                ident,
                POPP_YEAR,
                POPP_YEAR,
                POPP_AMOUNT,
                false,
                List.of(environmentName(environment)));
    }

    static AfpOffentligRequest afpRequest(String ident) {
        return new AfpOffentligRequest(
                List.of(),
                List.of(new AfpOffentligRequest.Mocksvar(
                        AFP_TP_ID,
                        ident,
                        AfpOffentligRequest.StatusAfp.INNVILGET,
                        LocalDate.of(2025, 1, 1),
                        2025,
                        List.of(new AfpOffentligRequest.DatoBeloep(
                                LocalDate.of(2025, 1, 1),
                                10000)))));
    }

    static PensjonsavtaleRequest pensjonsavtaleRequest(String ident) {
        return new PensjonsavtaleRequest(
                ident,
                PENSJONSAVTALE_PRODUCT,
                PensjonsavtaleRequest.AvtaleKategori.PRIVAT_TJENESTEPENSJON,
                List.of(new PensjonsavtaleRequest.Utbetalingsperiode(
                        62,
                        1,
                        72,
                        12,
                        30000)),
                PEN_ENVIRONMENTS);
    }
}
