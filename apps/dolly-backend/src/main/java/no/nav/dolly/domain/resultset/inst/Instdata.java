package no.nav.dolly.domain.resultset.inst;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@EqualsAndHashCode
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
}
