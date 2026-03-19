package no.nav.organisasjonforvalter.dto.responses.ereg;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class OrganisasjonBase {

    @Schema(description = "Organisasjonsnummer", example = "990983666")
    private String organisasjonsnummer;

    @Schema(description = "Informasjon om organisasjonsnavn")
    private Navn navn;
}
