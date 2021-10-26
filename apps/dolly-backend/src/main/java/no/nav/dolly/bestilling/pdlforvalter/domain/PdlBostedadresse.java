package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlBostedadresse extends PdlAdresse {

    private UkjentBosted ukjentBosted;
    private PdlMatrikkeladresse matrikkeladresse;
    private LocalDate angittFlyttedato;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UkjentBosted {

        private String bostedskommune;
    }
}
