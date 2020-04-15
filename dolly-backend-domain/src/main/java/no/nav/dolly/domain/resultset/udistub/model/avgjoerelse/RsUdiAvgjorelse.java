package no.nav.dolly.domain.resultset.udistub.model.avgjoerelse;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPeriode;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsUdiAvgjorelse {

    private LocalDateTime avgjoerelsesDato;
    private LocalDateTime effektueringsDato;
    private Boolean erPositiv;
    private String etat;
    private String grunntypeKode;
    private Boolean harFlyktningstatus;
    private LocalDateTime iverksettelseDato;
    private String saksnummer;
    private String tillatelseKode;
    private RsUdiPeriode tillatelsePeriode;
    private Integer tillatelseVarighet;
    private String tillatelseVarighetKode;
    private Boolean uavklartFlyktningstatus;
    private RsUdiPeriode utfallPeriode;
    private Integer utfallVarighet;
    private String utfallVarighetKode;
    private String utfallstypeKode;
    private LocalDateTime utreisefristDato;
}
