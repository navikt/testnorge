package no.nav.registre.testnorge.libs.dto.ameldingservice.v1;

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

    String arbeidsforholdId;
    String arbeidsforholdType;
    LocalDate startdato;
    LocalDate sluttdato;
    Float antallTimerPerUke;
    String yrke;
    String arbeidstidsordning;
    Float stillingsprosent;
    LocalDate sisteLoennsendringsdato;
    List<PermisjonDTO> permisjoner;
    FartoeyDTO fartoey;
    List<InntektDTO> inntekter;
    List<AvvikDTO> avvik;

    public List<AvvikDTO> getAvvik() {
        if (avvik == null) {
            avvik = new ArrayList<>();
        }
        return avvik;
    }

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
