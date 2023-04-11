package no.nav.dolly.bestilling.arenaforvalter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "NB! miljø og personident er påkrevd. Hvis ikke vedtaktype er angitt, tolkes det som O (ny rettighet)")
public class Aap115Request {

    @Schema(description = "Brukerens fødselsnummer")
    private String personident;

    @Schema(description = "Hvilket miljø endringen skal gjøres i")
    private String miljoe;

    @Schema(description = "AAP-§115-rettigheter for brukeren")
    private List<Aap115> nyeAap115;

    public List<Aap115> getNyeAap115() {
        if (isNull(nyeAap115)) {
            nyeAap115 = new ArrayList<>();
        }
        return nyeAap115;
    }
}
