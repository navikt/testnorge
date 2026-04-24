package no.nav.dolly.synt.dagpenger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.synt.dagpenger.dto.DagpengevedtakDto;
import no.nav.dolly.synt.dagpenger.model.ModelService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Syntetisering")
public class DagpengevedtakController {

    private final ModelService modelService;

    @PostMapping(value = "/vedtak/{rettighet}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "provider.controller.generate_vedtak",
            summary = "Trigger generering av dagpengevedtak",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Liste med startdatoer for vedtakene",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(type = "string")),
                            examples = @ExampleObject(value = "[\"2018-10-01\",\"2019-01-01\"]")
                    )
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DagpengevedtakDto.class)))
            )
    )
    public ResponseEntity<List<DagpengevedtakDto>> generateVedtak(
            @PathVariable
            @Parameter(
                    name = "rettighet",
                    required = true,
                    description = "Rettighet for vedtakene som skal genereres",
                    schema = @Schema(type = "string", allowableValues = {"PERM", "DAGO"})
            )
            String rettighet,
            @RequestBody List<String> startDates) {
        return ResponseEntity.ok(modelService.generateVedtak(rettighet, startDates));
    }
}
