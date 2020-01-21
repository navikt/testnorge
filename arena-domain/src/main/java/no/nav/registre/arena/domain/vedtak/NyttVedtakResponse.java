package no.nav.registre.arena.domain.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
            "nyeTiltaksdeltakelse", "nyeTiltakspenger"
    })
    private List<NyttVedtakTiltak> nyeRettigheterTiltak;

    @JsonAlias({
            "nyeTilleggsstonad"
    })
    private List<NyttVedtakTillegg> nyeRettigheterTillegg;

    @JsonAlias({
            "nyeAapFeilList", "nyeAap115FeilList", "nyeAaunguforFeilList", "nyeAatforFeilList", "nyeFritakFeilList",
            "nyeTiltaksdeltakelseFeilList", "nyeTiltakspengerFeilList",
            "nyeTilleggsstonadFeilList"
    })
    private List<NyttVedtakFeil> feiledeRettigheter;
}
