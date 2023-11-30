package no.nav.brregstub.api.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsOrganisasjon {

    @NotNull
    private Integer orgnr;
    @Builder.Default
    private Integer hovedstatus = 0;
    @Builder.Default
    private List<Integer> understatuser = new LinkedList<>();
    @NotNull
    private LocalDate registreringsdato;
    @Valid
    private RsSamendring kontaktperson;
    @Valid
    private RsSamendring sameier;
    @Valid
    private RsSamendring styre;
    @Valid
    private RsSamendring komplementar;
    @Valid
    private RsSamendring deltakere;
}
