package no.nav.dolly.domain.resultset.inst;

import java.time.LocalDateTime;

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
public class RsInstdata {

    private InstdataInstitusjonstype institusjonstype;
    private InstdataVarighet varighet;
    private LocalDateTime startdato;
    private LocalDateTime sluttdato;
    private InstdataKategori kategori;
    private InstdataKilde kilde;
    private String tssEksternId;
    private Boolean overfoert;
}
