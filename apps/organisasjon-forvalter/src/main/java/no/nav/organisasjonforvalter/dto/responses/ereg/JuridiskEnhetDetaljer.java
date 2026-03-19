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
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "enhetstype",
        "harAnsatte",
        "sektorkode",
        "registrertStiftelsesregisteret",
        "kapitalopplysninger",
        "foretaksregisterRegistreringer"
})
@SuppressWarnings({"squid:S1700", "findbugs:RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"})
public class JuridiskEnhetDetaljer {

    @Schema(description = "Sektorkode (kodeverk: Sektorkoder)", example = "6500")
    private String sektorkode;

    @Schema(description = "Enhetstype - juridisk enhet (kodeverk: EnhetstyperJuridiskEnhet)", example = "ENK")
    private String enhetstype;

    @Schema(description = "Er enhet regisrert i Stiftelsesregisteret?", example = "false")
    private Boolean registrertStiftelsesregisteret;

    @Schema(description = "Har enhet ansatte?", example = "true")
    private Boolean harAnsatte;

    @Schema(description = "Liste med kapitalopplysninger")
    private List<Kapitalopplysninger> kapitalopplysninger;

    @Schema(description = "Liste med registreringer i Foretaksregisteret")
    private List<Foretaksregister> foretaksregisterRegistreringer;

    public List<Kapitalopplysninger> getKapitalopplysninger() {
        if (kapitalopplysninger == null) {
            kapitalopplysninger = new ArrayList<>();
        }
        return this.kapitalopplysninger;
    }

    public List<Foretaksregister> getForetaksregisterRegistreringer() {
        if (foretaksregisterRegistreringer == null) {
            foretaksregisterRegistreringer = new ArrayList<>();
        }
        return this.foretaksregisterRegistreringer;
    }
}
