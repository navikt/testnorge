package no.nav.dolly.domain.resultset.udistub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.nav.dolly.domain.resultset.udistub.model.arbeidsadgang.RsUdiArbeidsadgang;
import no.nav.dolly.domain.resultset.udistub.model.opphold.RsUdiOppholdStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsUdiPerson {

    private List<RsUdiAlias> aliaser;
    private RsUdiArbeidsadgang arbeidsadgang;
    private RsUdiOppholdStatus oppholdStatus;
    private Boolean avgjoerelseUavklart;
    private Boolean harOppholdsTillatelse;
    private Boolean flyktning;
    private String soeknadOmBeskyttelseUnderBehandling;
    private LocalDateTime soknadDato;

    public List<RsUdiAlias> getAliaser() {
        if (isNull(aliaser)) {
            aliaser = new ArrayList<>();
        }
        return aliaser;
    }
}
