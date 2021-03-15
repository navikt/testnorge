package no.nav.dolly.domain.resultset.tpsf;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsForeldreRelasjonRequest {

    @Schema(required = true,
            description = "Ident for forelder")
    private String ident;

    private RsForeldreRequest.ForeldreType foreldreType;

    @Schema(description = "Når true får foreldre felles adresse med hverandre.")
    private Boolean harFellesAdresse;
}

