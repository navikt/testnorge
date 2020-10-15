package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsRelasjon extends RsTpsfBasisBestilling {

    @Schema(type = "String",
            description = "Ident (FNR/DNR/BOST). Feltet benyttes for legg-til/endre på person kun, ikke ny bestilling")
    private String ident;

    @Schema(description = "Identtype FNR/DNR/BOST, default er FNR")
    private String identtype;

    @Schema(description = "Velg alder på testperson. Personen vil beholde alder i minst tre, maksimum ni måneder")
    private Integer alder;

    @Schema(description = "Født etter denne dato. Dafault velges voksne personer i alder 30-60, mens barn får alder 0-18")
    private LocalDateTime foedtEtter;

    @Schema(description = "Født før denne dato. Dafault velges voksne personer i alder 30-60, mens barn får alder 0-18")
    private LocalDateTime foedtFoer;

    @Schema(description = "Kjønn på testperson. Gyldige verdier: 'K', 'M' og 'U'. "
            + "Ubestemt betyr at systemet velger for deg og generert person blir mann eller kvinne")
    private String kjonn;

    private List<RsIdenthistorikk> identHistorikk;

    @JsonIgnore
    public boolean isKjonnUkjent() {
        return "U".equals(getKjonn());
    }
}
