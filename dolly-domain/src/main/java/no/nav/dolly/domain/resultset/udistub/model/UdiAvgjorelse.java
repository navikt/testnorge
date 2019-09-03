package no.nav.dolly.domain.resultset.udistub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class UdiAvgjorelse {

    private String omgjortAvgjoerelsesId;
    private String utfallstypeKode;
    private String grunntypeKode;
    private String tillatelseKode;
    private Boolean erPositiv;
    private String utfallVarighetKode;
    private Integer utfallVarighet;
    private UdiPeriode utfallPeriode;
    private String tillatelseVarighetKode;
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
    @JsonBackReference
    private UdiPerson person;
}
