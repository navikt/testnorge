package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
