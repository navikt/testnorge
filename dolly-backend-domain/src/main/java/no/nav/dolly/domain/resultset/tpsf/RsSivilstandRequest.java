package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsSivilstandRequest {

    @Schema(required = true,
            description = "Sivilstand i hht kodeverk 'Sivilstander'")
    private String sivilstand;

    @Schema(required = true,
            type = "LocalDateTime",
            description = "Sivilstand gjelder fra denne dato")
    private LocalDateTime sivilstandRegdato;
}
