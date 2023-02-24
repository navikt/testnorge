package no.nav.brregstub.api.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

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
