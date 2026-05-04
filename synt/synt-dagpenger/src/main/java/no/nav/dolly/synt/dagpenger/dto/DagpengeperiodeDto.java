package no.nav.dolly.synt.dagpenger.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DagpengeperiodeDto {

    private Integer antallUkerPermittering;
    private String justertFomDato;
    private Integer antallUker;
    private String endringVentedagsteller;
    private String endringPeriodeteller;
    private String endringPermitteringsteller;
    private String begrunnelseTellerendring;
    private String nullstillPeriodeteller;
    private String nullstillPermitteringsteller;
}

