package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@UtilityClass
class PensjonTestData {

    static final String TP_ORDNING = "3010";
    static final int POPP_YEAR = 2020;
    static final int POPP_AMOUNT = 12345;
    static final String AFP_TP_ID = "4099";
    static final String PENSJONSAVTALE_PRODUCT = "Dollystatus syntetisk kontrakttest";
    static final List<String> PEN_ENVIRONMENTS = List.of("q1", "q2");

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

    static PensjonPersonRequest personRequest(String ident, FunctionalTestEnvironment environment) {
        return new PensjonPersonRequest(
                Set.of(environmentName(environment)),
                ident,
                foedselsdato(ident).toString(),
                "NOR");
    }

    static LocalDate foedselsdato(String ident) {
        var year = Integer.parseInt(ident.substring(4, 6));
        var individ = Integer.parseInt(ident.substring(6, 9));
        int century;
        if (individ < 500 || (individ >= 900 && year > 39)) {
            century = 1900;
        } else if (year < 40) {
            century = 2000;
        } else if (individ < 750 && year > 54) {
            century = 1800;
        } else {
            century = 2000;
        }
        return LocalDate.of(
                century + year,
                Integer.parseInt(ident.substring(2, 4)) % 20,
                Integer.parseInt(ident.substring(0, 2)) % 40);
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
