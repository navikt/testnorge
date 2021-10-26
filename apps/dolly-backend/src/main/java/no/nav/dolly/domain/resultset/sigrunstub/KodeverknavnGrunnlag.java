package no.nav.dolly.domain.resultset.sigrunstub;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KodeverknavnGrunnlag {
    private String tekniskNavn;
    private String verdi;
}

