package no.nav.dolly.domain.resultset.inst;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Instdata {

    private LocalDate sluttdato;
    private LocalDate forventetSluttdato;
    private InstdataInstitusjonstype institusjonstype;
    private InstdataKategori kategori;

    @EqualsAndHashCode.Exclude
    private InstdataKilde kilde;

    @EqualsAndHashCode.Exclude
    private String oppholdId;

    private Boolean overfoert;
    private String norskident;
    private LocalDate startdato;
    private String tssEksternId;

    private String oppholdstype;
    private String registrertAv;
}
