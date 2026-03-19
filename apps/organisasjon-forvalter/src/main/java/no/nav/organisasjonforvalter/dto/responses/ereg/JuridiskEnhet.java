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
@Schema(description = "Informasjon om juridisk enhet", allOf = Organisasjon.class)
@JsonPropertyOrder({
        "organisasjonsnummer",
        "type",
        "navn",
        "organisasjonDetaljer",
        "juridiskEnhetDetaljer",
        "driverVirksomheter",
        "bestaarAvOrgledd",
        "knytninger",
        "fisjoner",
        "fusjoner"
})
@SuppressWarnings({"squid:S1700", "findbugs:RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"})
public class JuridiskEnhet extends Organisasjon {

    public static final String TYPE = "JuridiskEnhet";

    @Schema(description = "Liste av hvilke organisasjonsledd juridisk enhet best&aring;r av")
    private List<BestaarAvOrganisasjonsledd> bestaarAvOrganisasjonsledd;

    @Schema(description = "Liste av virksomhet(er) som drives av juridisk enhet")
    private List<DriverVirksomhet> driverVirksomheter;

    @Schema(description = "Liste av fisjon(er) for juridisk enhet")
    private List<JuridiskEnhetFisjon> fisjoner;

    @Schema(description = "Liste av fusjon(er) for juridisk enhet")
    private List<JuridiskEnhetFusjon> fusjoner;

    @Schema(description = "Liste av knytninger for juridisk enhet")
    private List<JuridiskEnhetKnytning> knytninger;

    @Schema(description = "Liste av detaljer for juridisk enhet")
    private JuridiskEnhetDetaljer juridiskEnhetDetaljer;

    public List<BestaarAvOrganisasjonsledd> getBestaarAvOrganisasjonsledd() {
        if (bestaarAvOrganisasjonsledd == null) {
            bestaarAvOrganisasjonsledd = new ArrayList<>();
        }
        return this.bestaarAvOrganisasjonsledd;
    }

    public List<DriverVirksomhet> getDriverVirksomheter() {
        if (driverVirksomheter == null) {
            driverVirksomheter = new ArrayList<>();
        }
        return this.driverVirksomheter;
    }

    public List<JuridiskEnhetFisjon> getFisjoner() {
        if (fisjoner == null) {
            fisjoner = new ArrayList<>();
        }
        return this.fisjoner;
    }

    public List<JuridiskEnhetFusjon> getFusjoner() {
        if (fusjoner == null) {
            fusjoner = new ArrayList<>();
        }
        return this.fusjoner;
    }

    public List<JuridiskEnhetKnytning> getKnytninger() {
        if (knytninger == null) {
            knytninger = new ArrayList<>();
        }
        return this.knytninger;
    }

    public JuridiskEnhetDetaljer getJuridiskEnhetDetaljer() {
        return juridiskEnhetDetaljer;
    }

    public void setJuridiskEnhetDetaljer(JuridiskEnhetDetaljer value) {
        this.juridiskEnhetDetaljer = value;
    }
}
