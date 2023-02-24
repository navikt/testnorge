package no.nav.brregstub.api.v1;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import no.nav.brregstub.api.common.RsAdresse;
import no.nav.brregstub.api.common.RsNavn;

@Data
@NoArgsConstructor
public class RolleTo {

    @NotNull
    private LocalDate registreringsdato;

    @NotBlank
    private String rollebeskrivelse;

    @NotNull
    private Integer orgNr;

    private RsNavn foretaksNavn;

    private RsAdresse forretningsAdresse;

    private RsAdresse postAdresse;
}
