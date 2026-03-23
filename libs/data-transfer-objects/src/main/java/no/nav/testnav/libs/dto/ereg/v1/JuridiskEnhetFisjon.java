package no.nav.testnav.libs.dto.ereg.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "juridiskEnhet",
        "virkningsdato",
        "bruksperiode",
        "gyldighetsperiode"
})
public class JuridiskEnhetFisjon {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    private LocalDate virkningsdato;

    @Schema(description = "Informasjon om juridisk enhet")
    private JuridiskEnhet juridiskEnhet;
}
