package no.nav.brregstub.api.common;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RsNavn {

    @NotBlank
    private String navn1;
    private String navn2;
    private String navn3;

}
