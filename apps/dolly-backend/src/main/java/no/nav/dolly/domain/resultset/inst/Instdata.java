package no.nav.dolly.domain.resultset.inst;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Instdata {

    private String norskident;
    private String tssEksternId;

    private InstdataInstitusjonstype institusjonstype;
    private String oppholdstype;

    private LocalDate startdato;
    private LocalDate sluttdato;
    private LocalDate forventetSluttdato;

    private String registrertAv;
}
