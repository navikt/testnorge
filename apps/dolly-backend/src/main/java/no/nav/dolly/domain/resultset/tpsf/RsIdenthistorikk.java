package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
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
public class RsIdenthistorikk {

    @Schema(description = "Identtype FNR/DNR/BOST, default er FNR")
    private String identtype;

    @Schema(description = "Født etter denne dato. Dafault velges voksne personer i alder 30-60, mens barn får alder 0-18")
    private LocalDateTime foedtEtter;

    @Schema(description = "Født før denne dato. Dafault velges voksne personer i alder 30-60, mens barn får alder 0-18")
    private LocalDateTime foedtFoer;

    @Schema(description = "Kjønn på testperson. Gyldige verdier: 'K', 'M' og 'U'. Ubestemt betyr at systemet velger for deg og generert person blir mann eller kvinne")
    private String kjonn;

    @Schema(description = "Registreringsdato for endring av denne ident")
    private LocalDateTime regdato;
}