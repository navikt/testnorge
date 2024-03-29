package no.nav.brregstub.api.v2;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import no.nav.brregstub.api.common.Egenskap;

@Data
@NoArgsConstructor
public class RsRolleStatus {

    @NotBlank
    private Egenskap egenskap;

    private boolean fratraadt;
}
