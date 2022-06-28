package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dagpengeperiode {
    private Integer antallUkerPermittering;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate justertFomDato;

    private Integer antallUker;

    private String endringVentedagsteller;

    private String endringPeriodeteller;

    private String endringPermitteringsteller;

    private String begrunnelseTellerendring;

    private String nullstillPeriodeteller;

    private String nullstillPermitteringsteller;
}
