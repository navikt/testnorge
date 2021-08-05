package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "opprettetTidspunkt", "opprettetAv", "opprettetKilde", "opprettetKildereferanse", "endretTidspunkt", "endretAv", "endretKilde", "endretKildereferanse" })
@ApiModel(
        description = "Informasjon om opprettelse og endring av objekt. MERK: Skal IKKE eksponeres i selvbetjeningssonen (SBS)."
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Sporingsinformasjon {

    private LocalDateTime opprettetTidspunkt;
    @ApiModelProperty(
            notes = "Brukernavn for opprettelse",
            example = "srvappserver"
    )
    private String opprettetAv;
    @ApiModelProperty(
            notes = "Kilde for opprettelse",
            example = "EDAG"
    )
    private String opprettetKilde;
    @ApiModelProperty(
            notes = "Kildereferanse for opprettelse",
            example = "22a26849-aeef-4b81-9174-e238c11e1081"
    )
    private String opprettetKildereferanse;
    private LocalDateTime endretTidspunkt;
    @ApiModelProperty(
            notes = "Brukernavn for endring",
            example = "Z990693"
    )
    private String endretAv;
    @ApiModelProperty(
            notes = "Kilde for endring",
            example = "AAREG"
    )
    private String endretKilde;
    @ApiModelProperty(
            notes = "Kildereferanse for endring",
            example = "referanse-fra-kilde"
    )
    private String endretKildereferanse;

    @JsonProperty("opprettetTidspunkt")
    @ApiModelProperty(
            notes = "Tidspunkt for opprettelse, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]",
            example = "2018-09-19T12:10:58.059"
    )
    public String getOpprettetTidspunktAsString() {
        return JavaTimeUtil.toString(this.opprettetTidspunkt);
    }

    @JsonProperty("endretTidspunkt")
    @ApiModelProperty(
            notes = "Tidspunkt for endring, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]",
            example = "2018-09-19T12:11:20.79"
    )
    public String getEndretTidspunktAsString() {
        return JavaTimeUtil.toString(this.endretTidspunkt);
    }
}
