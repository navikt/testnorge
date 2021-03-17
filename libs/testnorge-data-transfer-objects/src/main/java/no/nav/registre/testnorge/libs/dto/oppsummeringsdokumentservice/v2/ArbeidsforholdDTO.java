package no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdDTO {
    @JsonProperty
    String arbeidsforholdId;
    @JsonProperty
    String typeArbeidsforhold;
    @JsonProperty(required = true)
    LocalDate startdato;
    @JsonProperty
    LocalDate sluttdato;
    @JsonProperty
    Float antallTimerPerUke;
    @JsonProperty(required = true)
    String yrke;
    @JsonProperty
    String arbeidstidsordning;
    @JsonProperty
    Float stillingsprosent;
    @JsonProperty
    LocalDate sisteLoennsendringsdato;
    @JsonProperty
    List<PermisjonDTO> permisjoner;
    @JsonProperty
    FartoeyDTO fartoey;
    @JsonProperty
    List<InntektDTO> inntekter;

    public List<PermisjonDTO> getPermisjoner() {
        if (permisjoner == null) {
            permisjoner = new ArrayList<>();
        }
        return permisjoner;
    }

    public List<InntektDTO> getInntekter() {
        if (inntekter == null) {
            inntekter = new ArrayList<>();
        }
        return inntekter;
    }
}
