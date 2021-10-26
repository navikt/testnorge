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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsTpsfBasisMedSivilstandBestilling extends RsTpsfBasisBestilling {

    @Schema(description = "Sivilstand i hht kodeverk 'Sivilstander'")
    private String sivilstand;

    @Schema(type = "LocalDateTime",
            description = "Dato sivilstand. Hvis blankt settes dagens dato")
    private LocalDateTime sivilstandRegdato;

    private RsSimpleRelasjoner relasjoner;
}
