package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlBostedadresse extends PdlAdresse {

    private PdlVegadresse vegadresse;
    private UkjentBosted ukjentBosted;
    private PdlMatrikkeladresse matrikkeladresse;
    private PdlUtenlandskAdresse utenlandskAdresse;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UkjentBosted implements Serializable {

        private String bostedskommune;
    }
}
