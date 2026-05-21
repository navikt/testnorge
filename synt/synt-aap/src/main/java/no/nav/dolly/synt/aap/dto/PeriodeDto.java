package no.nav.dolly.synt.aap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodeDto {

    @JsonProperty("ENDRING_PERIODE")
    private String endringPeriode;

    @JsonProperty("ENDRING_PERIODE_BEGRUNNELSE")
    private String endringPeriodeBegrunnelse;

    @JsonProperty("ENDRING_UNNTAK")
    private String endringUnntak;

    @JsonProperty("ENDRING_UNNTAK_BEGRUNNELSE")
    private String endringUnntakBegrunnelse;

    @JsonProperty("NULLSTILL")
    private String nullstill;

    @JsonProperty("PERIODE_KODE")
    private String periodeKode;
}

