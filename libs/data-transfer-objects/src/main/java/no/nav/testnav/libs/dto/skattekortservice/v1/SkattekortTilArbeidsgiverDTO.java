package no.nav.testnav.libs.dto.skattekortservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkattekortTilArbeidsgiverDTO {

    private List<Arbeidsgiver> arbeidsgiver;

    public List<Arbeidsgiver> getArbeidsgiver() {

        if (isNull(arbeidsgiver)) {
            arbeidsgiver = new ArrayList<>();
        }
        return arbeidsgiver;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsgiver {

        private IdentifikatorForEnhetEllerPerson arbeidsgiveridentifikator;
        private List<Skattekortmelding> arbeidstaker;

        public List<Skattekortmelding> getArbeidstaker() {

            if (isNull(arbeidstaker)) {
                arbeidstaker = new ArrayList<>();
            }
            return arbeidstaker;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Skattekortmelding {

        private String arbeidstakeridentifikator;
        private Resultatstatus resultatPaaForespoersel;
        private Skattekort skattekort;
        private List<Tilleggsopplysning> tilleggsopplysning;
        private Integer inntektsaar;

        public List<Tilleggsopplysning> getTilleggsopplysning() {

            if (isNull(tilleggsopplysning)) {
                tilleggsopplysning = new ArrayList<>();
            }
            return tilleggsopplysning;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentifikatorForEnhetEllerPerson {

        private String organisasjonsnummer;
        private String personidentifikator;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Skattekort {

        private LocalDate utstedtDato;
        private Long skattekortidentifikator;
        private List<Trekktype> trekktype;

        public List<Trekktype> getTrekktype() {

            if (isNull(trekktype)) {
                trekktype = new ArrayList<>();
            }
            return trekktype;
        }
    }
}
