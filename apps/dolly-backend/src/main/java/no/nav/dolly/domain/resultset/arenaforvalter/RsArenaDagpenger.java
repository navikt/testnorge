package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsArenaDagpenger {


    @Schema(required = true,
            type = "String")
    private String rettighetKode;

    @Schema(required = true,
            type = "LocalDateTime")
    private LocalDateTime fraDato;

    @Schema(type = "LocalDateTime")
    private LocalDateTime tilDato;

    @Schema(type = "LocalDateTime")
    private LocalDateTime mottattDato;
}