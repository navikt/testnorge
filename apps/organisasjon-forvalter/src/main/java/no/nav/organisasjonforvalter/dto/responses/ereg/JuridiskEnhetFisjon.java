package no.nav.organisasjonforvalter.dto.responses.ereg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import no.nav.organisasjonforvalter.util.JavaTimeUtil;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "juridiskEnhet",
        "virkningsdato",
        "bruksperiode",
        "gyldighetsperiode"
})
public class JuridiskEnhetFisjon {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    private LocalDate virkningsdato;

    @Schema(description = "Informasjon om juridisk enhet")
    private JuridiskEnhet juridiskEnhet;

    @JsonProperty("virkningsdato")
    @Schema(description = "Virkningsdato for fisjon, format (ISO-8601): yyyy-MM-dd", example = "2015-05-31")
    public String getVirkningsdatoAsString() {
        return JavaTimeUtil.toString(virkningsdato);
    }
}
