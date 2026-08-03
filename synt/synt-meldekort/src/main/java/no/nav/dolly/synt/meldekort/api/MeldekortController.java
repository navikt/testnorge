package no.nav.dolly.synt.meldekort.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.meldekort.onnx.MeldekortType;
import no.nav.dolly.synt.meldekort.onnx.OnnxService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Syntetisering")
@Slf4j
class MeldekortController {

    private final OnnxService onnxService;

    @GetMapping(value = "/meldekort/{meldegruppe}/{num_to_generate}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "provider.controller.generate_data",
            summary = "Trigger generering av meldekort",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Liste med meldekort på XML format",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "string")))
            )
    )
    List<String> generateData(
            @PathVariable
            @Parameter(
                    name = "meldegruppe",
                    required = true,
                    description = "Meldegruppen som skal genereres",
                    schema = @Schema(type = "string", allowableValues = {"ARBS", "ATTF", "INDIV", "DAGP", "FY"})
            )
            MeldekortType meldegruppe,
            @PathVariable("num_to_generate")
            @Parameter(
                    name = "num_to_generate",
                    required = true,
                    description = "Antall meldekort som skal genereres",
                    schema = @Schema(type = "integer")
            )
            Integer numToGenerate,
            @RequestParam(value = "arbeidstimer", required = false)
            @Parameter(
                    name = "arbeidstimer",
                    description = "Verdi som overskriver arbeidstimer verdien i genererte meldekort",
                    schema = @Schema(type = "number")
            )
            String arbeidstimer) {

        if (numToGenerate < 0) {
            throw new IllegalArgumentException("num_to_generate must be zero or positive");
        }

        log.info(
                "Generating {} meldekort for meldegruppe {}...",
                numToGenerate,
                meldegruppe
        );
        var started = System.currentTimeMillis();
        var arbeidstimerOverride = parseArbeidstimer(arbeidstimer);
        var generated = onnxService.generateMeldekort(meldegruppe, numToGenerate, arbeidstimerOverride);
        log.info(
                "Generated {} meldekort for meldegruppe {} in {}ms",
                generated.size(),
                meldegruppe,
                System.currentTimeMillis() - started
        );
        return generated;

    }

    private static Double parseArbeidstimer(String arbeidstimer) {

        if (arbeidstimer == null || arbeidstimer.isBlank()) {
            return null;
        }
        try {
            var parsed = Double.parseDouble(arbeidstimer);
            if (parsed < 0 || Double.isNaN(parsed) || Double.isInfinite(parsed)) {
                return null;
            }
            return parsed;
        } catch (NumberFormatException ignored) {
            return null;
        }

    }

}

