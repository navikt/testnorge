package no.nav.dolly.domain.resultset.udistub.model.avgjoerelse;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.UdiPeriode;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UdiAvgjorelse {

    private LocalDate avgjoerelsesDato;
    private LocalDate effektueringsDato;
    private Boolean erPositiv;
    private String etat;
    private String grunntypeKode;
    private Boolean harFlyktningstatus;
    private LocalDate iverksettelseDato;
    private String saksnummer;
    private String tillatelseKode;
    private UdiPeriode tillatelsePeriode;
    private Integer tillatelseVarighet;
    private String tillatelseVarighetKode;
    private Boolean uavklartFlyktningstatus;
    private UdiPeriode utfallPeriode;
    private Integer utfallVarighet;
    private String utfallVarighetKode;
    private String utfallstypeKode;
    private LocalDate utreisefristDato;
}
