package no.nav.dolly.bestilling.sykemelding.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.sykemelding.SykmeldingType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SykemeldingRequestDTO {

    private SykmeldingType type;
    private String ident;
    private List<Aktivitet> aktivitet;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Aktivitet {
        private Integer grad;
        private Boolean reisetilskudd;
        private LocalDate fom;
        private LocalDate tom;
    }
}
