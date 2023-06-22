package no.nav.dolly.domain.resultset.arenaforvalter;

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
public class RsArenaDagpenger {


    @Schema(
            type = "String")
    private String rettighetKode;

    @Schema(
            type = "LocalDateTime")
    private LocalDateTime fraDato;

    @Schema(type = "LocalDateTime")
    private LocalDateTime tilDato;

    @Schema(type = "LocalDateTime")
    private LocalDateTime mottattDato;
}