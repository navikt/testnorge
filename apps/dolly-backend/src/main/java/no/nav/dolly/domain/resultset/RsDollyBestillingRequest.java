package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyBestillingRequest extends RsDollyUtvidetBestilling {

    @Schema(description = "Antall testpersoner som bestilles")
    private int antall;

    @JsonIgnore
    private String feil;
}