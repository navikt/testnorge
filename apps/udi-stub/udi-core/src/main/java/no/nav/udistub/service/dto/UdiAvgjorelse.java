package no.nav.udistub.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import no.nav.udistub.database.model.Kodeverk;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UdiAvgjorelse {

    private Kodeverk utfallstypeKode;
    private Kodeverk grunntypeKode;
    private Kodeverk tillatelseKode;
    private Boolean erPositiv;
    private Kodeverk utfallVarighetKode;
    private Integer utfallVarighet;
    private UdiPeriode utfallPeriode;
    private Kodeverk tillatelseVarighetKode;
    private Integer tillatelseVarighet;
    private UdiPeriode tillatelsePeriode;
    private LocalDate effektueringsDato;
    private LocalDate avgjoerelsesDato;
    private LocalDate iverksettelseDato;
    private LocalDate utreisefristDato;
    private String saksnummer;
    private String etat;
    private Boolean harFlyktningstatus;
    private Boolean uavklartFlyktningstatus;
}
