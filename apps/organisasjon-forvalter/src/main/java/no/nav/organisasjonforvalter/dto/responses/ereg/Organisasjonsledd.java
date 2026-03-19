package no.nav.organisasjonforvalter.dto.responses.ereg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Informasjon om organisasjonsledd", allOf = Organisasjon.class)
@JsonPropertyOrder({
        "organisasjonsnummer",
        "type",
        "navn",
        "organisasjonDetaljer",
        "organisasjonsleddDetaljer",
        "driverVirksomheter",
        "inngaarIJuridiskEnheter",
        "organisasjonsleddUnder",
        "organisasjonsleddOver"
})
@SuppressWarnings("findbugs:RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
public class Organisasjonsledd extends Organisasjon {

    public static final String TYPE = "Organisasjonsledd";

    @Schema(description = "Liste av hvilke organisasjonsledd som ligger under organisasjonsledd")
    private List<BestaarAvOrganisasjonsledd> organisasjonsleddUnder;

    @Schema(description = "Liste av hvilke organisasjonsledd som ligger over organisasjonsledd")
    private List<BestaarAvOrganisasjonsledd> organisasjonsleddOver;

    @Schema(description = "Liste av virksomhet(er) som drives av organisasjonsledd")
    private List<DriverVirksomhet> driverVirksomheter;

    @Schema(description = "Liste av hvilke(n) juridisk enhet organisasjonsledd inng&aring;r i")
    private List<InngaarIJuridiskEnhet> inngaarIJuridiskEnheter;

    @Schema(description = "Liste av detaljer for organisasjonsledd")
    private OrganisasjonsleddDetaljer organisasjonsleddDetaljer;

    public List<BestaarAvOrganisasjonsledd> getOrganisasjonsleddUnder() {
        if (organisasjonsleddUnder == null) {
            organisasjonsleddUnder = new ArrayList<>();
        }
        return this.organisasjonsleddUnder;
    }

    public List<BestaarAvOrganisasjonsledd> getOrganisasjonsleddOver() {
        if (organisasjonsleddOver == null) {
            organisasjonsleddOver = new ArrayList<>();
        }
        return this.organisasjonsleddOver;
    }

    public List<DriverVirksomhet> getDriverVirksomheter() {
        if (driverVirksomheter == null) {
            driverVirksomheter = new ArrayList<>();
        }
        return this.driverVirksomheter;
    }

    public List<InngaarIJuridiskEnhet> getInngaarIJuridiskEnheter() {
        if (inngaarIJuridiskEnheter == null) {
            inngaarIJuridiskEnheter = new ArrayList<>();
        }
        return this.inngaarIJuridiskEnheter;
    }
}
