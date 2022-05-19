package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
public class Dagpengeperiode {
    @JsonProperty
    private Integer antallUkerPermittering;

    @JsonProperty
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate justertFomDato;

    @JsonProperty
    private Integer antallUker;

    @JsonProperty
    private String endringVentedagsteller;

    @JsonProperty
    private String endringPeriodeteller;

    @JsonProperty
    private String endringPermitteringsteller;

    @JsonProperty
    private String begrunnelseTellerendring;

    @JsonProperty
    private String nullstillPeriodeteller;

    @JsonProperty
    private String nullstillPermitteringsteller;
}
