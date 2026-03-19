package no.nav.organisasjonforvalter.dto.responses.ereg;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Informasjon om organisasjon, subtyper: Virksomhet/JuridiskEnhet/Organisasjonsledd",
        allOf = OrganisasjonBase.class,
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = Virksomhet.TYPE, schema = Virksomhet.class),
                @DiscriminatorMapping(value = JuridiskEnhet.TYPE, schema = JuridiskEnhet.class),
                @DiscriminatorMapping(value = Organisasjonsledd.TYPE, schema = Organisasjonsledd.class)
        })
public class Organisasjon extends OrganisasjonBase {

    @Schema(description = "Detaljinformasjon for organisasjon")
    private OrganisasjonDetaljer organisasjonDetaljer;

    @Schema(name = "Organisasjonstype: Virksomhet/JuridiskEnhet/Organisasjonsledd", example = "Virksomhet")
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
