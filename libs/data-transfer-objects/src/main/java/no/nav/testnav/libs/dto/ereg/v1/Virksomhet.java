package no.nav.testnav.libs.dto.ereg.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Setter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Informasjon om virksomhet", allOf = Organisasjon.class)
@JsonPropertyOrder({
        "organisasjonsnummer",
        "type",
        "navn",
        "organisasjonDetaljer",
        "virksomhetDetaljer",
        "bestaarAvOrganisasjonsledd",
        "inngaarIJuridiskEnheter"
})
@SuppressWarnings("findbugs:RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
public class Virksomhet extends Organisasjon {

    public static final String TYPE = "Virksomhet";

    @Schema(description = "Liste av hvilke organisasjonsledd virksomhet best&aring;r av")
    private List<BestaarAvOrganisasjonsledd> bestaarAvOrganisasjonsledd;

    @Schema(description = "Liste av hvilke(n) juridisk enhet virksomhet inng&aring;r i")
    private List<InngaarIJuridiskEnhet> inngaarIJuridiskEnheter;

    @Getter
    @Schema(description = "Liste av detaljer for virksomhet")
    private VirksomhetDetaljer virksomhetDetaljer;

    public List<BestaarAvOrganisasjonsledd> getBestaarAvOrganisasjonsledd() {
        if (isNull(bestaarAvOrganisasjonsledd)) {
            bestaarAvOrganisasjonsledd = new ArrayList<>();
        }
        return this.bestaarAvOrganisasjonsledd;
    }

    public List<InngaarIJuridiskEnhet> getInngaarIJuridiskEnheter() {
        if (isNull(inngaarIJuridiskEnheter)) {
            inngaarIJuridiskEnheter = new ArrayList<>();
        }
        return this.inngaarIJuridiskEnheter;
    }
}
