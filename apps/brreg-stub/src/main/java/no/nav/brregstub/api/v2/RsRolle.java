package no.nav.brregstub.api.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.api.common.RsAdresse;
import no.nav.brregstub.api.common.RsNavn;

@Data
@NoArgsConstructor
public class RsRolle {

    @NotNull
    private LocalDate registreringsdato;

    @NotNull
    private RolleKode rolle;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rollebeskrivelse;

    @NotNull
    private Integer orgNr;

    private RsNavn foretaksNavn;

    private RsAdresse forretningsAdresse;

    private RsAdresse postAdresse;

    private List<RsRolleStatus> personRolle;
}
