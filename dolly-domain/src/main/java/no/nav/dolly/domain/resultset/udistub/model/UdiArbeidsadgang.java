package no.nav.dolly.domain.resultset.udistub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UdiArbeidsadgang {

    private String harArbeidsAdgang;
    private String typeArbeidsadgang;
    private String arbeidsOmfang;
    private UdiPeriode periode;
    @JsonBackReference
    private UdiPerson person;
}
