package no.nav.registre.udistub.core.service.to;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UdiAlias {

    private String fnr;
    private UdiPersonNavn navn;
    @JsonBackReference
    private UdiPerson person;
}
