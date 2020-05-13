package no.nav.dolly.domain.resultset.udistub.model;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.nav.dolly.domain.resultset.udistub.model.arbeidsadgang.RsUdiArbeidsadgang;
import no.nav.dolly.domain.resultset.udistub.model.avgjoerelse.RsUdiAvgjorelse;
import no.nav.dolly.domain.resultset.udistub.model.opphold.RsUdiOppholdStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsUdiPerson {

    private List<RsUdiAvgjorelse> avgjoerelser;
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
