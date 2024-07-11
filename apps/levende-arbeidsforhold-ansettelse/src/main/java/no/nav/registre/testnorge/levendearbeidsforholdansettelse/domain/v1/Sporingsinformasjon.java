package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.v1.util.JavaTimeUtil;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "opprettetTidspunkt",
        "opprettetAv",
        "opprettetKilde",
        "opprettetKildereferanse",
        "endretTidspunkt",
        "endretAv",
        "endretKilde",
        "endretKildereferanse"
})
@Schema(description = "Informasjon om opprettelse og endring av objekt. MERK: Skal IKKE eksponeres i selvbetjeningssonen (SBS).")
public class Sporingsinformasjon {

    private LocalDateTime opprettetTidspunkt;

    @Schema(description = "Brukernavn for opprettelse", example = "srvappserver")
    private String opprettetAv;

    @Schema(description = "Kilde for opprettelse", example = "EDAG")
    private String opprettetKilde;

    @Schema(description = "Kildereferanse for opprettelse", example = "22a26849-aeef-4b81-9174-e238c11e1081")
    private String opprettetKildereferanse;

    private LocalDateTime endretTidspunkt;

    @Schema(description = "Brukernavn for endring", example = "Z990693")
    private String endretAv;

    @Schema(description = "Kilde for endring", example = "AAREG")
    private String endretKilde;

    @Schema(description = "Kildereferanse for endring", example = "referanse-fra-kilde")
    private String endretKildereferanse;

    @JsonIgnore
    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    @JsonIgnore
    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
    }

    @JsonProperty("opprettetTidspunkt")
    @Schema(description = "Tidspunkt for opprettelse, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]", example = "2018-09-19T12:10:58.059")
    public String getOpprettetTidspunktAsString() {
        return JavaTimeUtil.toString(opprettetTidspunkt);
    }

    @JsonProperty("opprettetTidspunkt")
    public void setOpprettetTidspunktAsString(String opprettetTidspunkt) {
        this.opprettetTidspunkt = JavaTimeUtil.toLocalDateTime(opprettetTidspunkt);
    }

    @JsonProperty("endretTidspunkt")
    @Schema(description = "Tidspunkt for endring, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]", example = "2018-09-19T12:11:20.79")
    public String getEndretTidspunktAsString() {
        return JavaTimeUtil.toString(endretTidspunkt);
    }

    @JsonProperty("endretTidspunkt")
    public void setEndretTidspunktAsString(String endretTidspunkt) {
        this.endretTidspunkt = JavaTimeUtil.toLocalDateTime(endretTidspunkt);
    }
}
