package no.nav.dolly.domain.resultset.inst;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instdata {

    private LocalDate faktiskSluttdato;
    private LocalDate forventetSluttdato;
    private InstdataInstitusjonstype institusjonstype;
    private InstdataKategori kategori;
    private InstdataKilde kilde;
    private String oppholdId;
    private Boolean overfoert;
    private String personident;
    private LocalDate startdato;
    private String tssEksternId;
    private InstdataVarighet varighet;
}
