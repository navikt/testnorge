package no.nav.registre.arena.core.consumer.rs.responses.rettighet.UngUfoer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UngUfoerSyntResponse {

    @JsonProperty("AAFOR36")
    private String aafor36;

    @JsonProperty("AANEDSSL")
    private String aanedssl;

    @JsonProperty("AASSLDOK")
    private String aassldok;

    @JsonProperty("AAUNGNEDS")
    private String aaungneds;

    @JsonProperty("AVBRUDDSKODE")
    private String avbruddskode;

    @JsonProperty("DATO_MOTTATT")
    private LocalDate datoMottatt;

    @JsonProperty("FRA_DATO")
    private LocalDate fraDato;

    @JsonProperty("UTFALL")
    private String utfall;

    @JsonProperty("VEDTAKTYPE")
    private String vedtaktype;
}
