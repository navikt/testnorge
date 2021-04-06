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
public class InntektDTO {
    LocalDate startdatoOpptjeningsperiode;
    LocalDate sluttdatoOpptjeningsperiode;
    Integer antall;
    String opptjeningsland;
    List<AvvikDTO> avvik;

    public List<AvvikDTO> getAvvik() {
        if (avvik == null) {
            avvik = new ArrayList<>();
        }
        return avvik;
    }
}
