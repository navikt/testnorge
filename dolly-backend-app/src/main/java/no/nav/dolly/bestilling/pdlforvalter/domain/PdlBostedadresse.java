package no.nav.dolly.bestilling.pdlforvalter.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlBostedadresse extends PdlAdresse {

    private UkjentBosted ukjentBosted;
    private PdlMatrikkeladresse matrikkeladresse;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UkjentBosted {

        private String bostedskommune;
    }
}
