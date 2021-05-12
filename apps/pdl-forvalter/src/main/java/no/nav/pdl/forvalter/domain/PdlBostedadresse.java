package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlBostedadresse extends PdlAdresse {

    private PdlVegadresse vegadresse;
    private PdlUkjentBosted ukjentBosted;
    private PdlMatrikkeladresse matrikkeladresse;
    private PdlUtenlandskAdresse utenlandskAdresse;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlUkjentBosted implements Serializable {

        private String bostedskommune;
    }
}
