package no.nav.brregstub.api.common;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
