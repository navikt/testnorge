package no.nav.dolly.domain.resultset.sykemelding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsSykemelding {

    private RsNySykemelding nySykemelding;

    @JsonIgnore
    public boolean hasNySykemelding() {

        return nonNull(nySykemelding);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsNySykemelding {

        private SykmeldingType type;
        private List<Aktivitet> aktivitet;

        public List<Aktivitet> getAktivitet() {
            if (isNull(aktivitet)) {
                aktivitet = new ArrayList<>();
            }
            return aktivitet;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Aktivitet {

            private Integer grad;
            private Boolean reisetilskudd;
            private LocalDate fom;
            private LocalDate tom;
        }
    }
}
