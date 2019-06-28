package no.nav.dolly.domain.resultset.tpsf;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Sivilstatus {

    private String sivilstand;
    private String datoSivilstand;
    private RsRelasjon personRelasjonMed;
}
