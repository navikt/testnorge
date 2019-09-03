package no.nav.registre.udistub.core.service.to;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.udistub.core.service.to.opphold.OppholdStatusTo;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class PersonTo {

    private String ident;
    private PersonNavnTo navn;
    private LocalDate foedselsDato;

    @JsonManagedReference
    private List<AvgjorelseTo> avgjoerelser;

    @JsonManagedReference
    private List<AliasTo> aliaser;

    @JsonManagedReference
    private ArbeidsadgangTo arbeidsadgang;

    @JsonManagedReference
    private OppholdStatusTo oppholdStatus;

    private Boolean avgjoerelseUavklart;
    private Boolean harOppholdsTillatelse;
    private Boolean flyktning;
    private JaNeiUavklart soeknadOmBeskyttelseUnderBehandling;

    private LocalDate soknadDato;
}
