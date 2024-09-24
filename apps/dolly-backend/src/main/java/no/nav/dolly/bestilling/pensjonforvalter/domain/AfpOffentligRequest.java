package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AfpOffentligRequest {

    private List<String> direktekall;
    private List<AfpOffentligStub> mocksvar;

    public List<String> getDirektekall() {

        if (isNull(direktekall)) {
            direktekall = new ArrayList<>();
        }
        return direktekall;
    }

    public List<AfpOffentligStub> getMocksvar() {

        if (isNull(mocksvar)) {
            mocksvar = new ArrayList<>();
        }
        return mocksvar;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AfpOffentligStub {

        private String tpId;
        private String fnr;
        private StatusAfp statusAfp;
        private LocalDate virkningsDato;
        private Integer sistBenyttetG;
        private List<DatoBeloep> belopsListe;

        public List<DatoBeloep> getBelopsListe() {

            if (isNull(belopsListe)) {
                belopsListe = new ArrayList<>();
            }
            return belopsListe;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatoBeloep {

        private LocalDate fomDato;
        private Integer belop;
    }

    public enum StatusAfp {UKJENT, INNVILGET, SOKT, AVSLAG, IKKE_SOKT}
}
