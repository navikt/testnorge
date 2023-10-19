package no.nav.brregstub.api.common;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsAdresse {

    @NotBlank
    private String adresse1;
    private String adresse2;
    private String adresse3;
    private String postnr;
    private String poststed;
    @NotBlank
    private String landKode;
    private String kommunenr;

}
