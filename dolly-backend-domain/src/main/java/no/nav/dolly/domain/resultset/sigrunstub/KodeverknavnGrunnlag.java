package no.nav.dolly.domain.resultset.sigrunstub;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KodeverknavnGrunnlag {
    private String tekniskNavn;
    private String verdi;
}

