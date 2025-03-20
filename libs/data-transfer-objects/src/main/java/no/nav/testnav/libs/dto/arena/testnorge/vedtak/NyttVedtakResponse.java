package no.nav.testnav.libs.dto.arena.testnorge.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NyttVedtakResponse {

    @JsonAlias({
            "nyeAap", "nyeAap115", "nyeAaungufor", "nyeAatfor", "nyeFritak"
    })
    private List<NyttVedtakAap> nyeRettigheterAap;

    @JsonAlias({
            "nyeTiltaksdeltakelse", "nyeTiltakspenger", "nyeTilleggsytelser", "nyeEndreDeltakerstatus",
            "nyeTiltaksaktivitet", "nyeFinntiltakResponse"
    })
    private List<NyttVedtakTiltak> nyeRettigheterTiltak;

    @JsonAlias({
            "nyeTilleggsstonad"
    })
    private List<NyttVedtakTillegg> nyeRettigheterTillegg;

    @JsonAlias({
            "nyeAapFeilList", "nyeAap115FeilList", "nyeAaunguforFeilList", "nyeAatforFeilList", "nyeFritakFeilList",
            "nyeTiltaksdeltakelseFeilList", "nyeTiltakspengerFeilList", "nyeTilleggsytelserFeilList",
            "nyeTilleggsstonadFeilList", "nyeEndreDeltakerstatusFeilList", "nyeTiltaksaktivitetFeilList",
            "nyeFinntiltakFeilList"
    })
    private List<NyttVedtakFeil> feiledeRettigheter;

    public List<NyttVedtakAap> getNyeRettigheterAap() {
        if (isNull(nyeRettigheterAap)){
            nyeRettigheterAap = new ArrayList<>();
        }
        return nyeRettigheterAap;
    }

    public List<NyttVedtakTiltak> getNyeRettigheterTiltak() {
        if (isNull(nyeRettigheterTiltak)){
            nyeRettigheterTiltak = new ArrayList<>();
        }
        return nyeRettigheterTiltak;
    }

    public List<NyttVedtakTillegg> getNyeRettigheterTillegg() {
        if (isNull(nyeRettigheterTillegg)){
            nyeRettigheterTillegg = new ArrayList<>();
        }
        return nyeRettigheterTillegg;
    }

    public List<NyttVedtakFeil> getFeiledeRettigheter() {
        if (isNull(feiledeRettigheter)){
            feiledeRettigheter = new ArrayList<>();
        }
        return feiledeRettigheter;
    }
}
