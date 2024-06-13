package no.nav.testnav.libs.dto.skattekortservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

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

        @JsonIgnore
        public boolean isEmptyArbeidstakeridentifikator() {

            return isBlank(arbeidstakeridentifikator);
        }

        @JsonIgnore
        public boolean isEmptyInntektsaar() {

            return isNull(inntektsaar);
        }
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
