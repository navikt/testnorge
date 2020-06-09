package no.nav.registre.testnorge.domain.dto.arena.testnorge.historikk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vedtakshistorikk {

    @JsonProperty("AAP")
    private List<NyttVedtakAap> aap;

    @JsonProperty("AA115")
    private List<NyttVedtakAap> aap115;

    @JsonProperty("AAUNGUFOR")
    private List<NyttVedtakAap> ungUfoer;

    @JsonProperty("AATFOR")
    private List<NyttVedtakAap> tvungenForvaltning;

    @JsonProperty("FRI_MK")
    private List<NyttVedtakAap> fritakMeldekort;

    @JsonProperty("BASI")
    private List<NyttVedtakTiltak> tiltakspenger;

    @JsonProperty("BTIL")
    private List<NyttVedtakTiltak> barnetillegg;

    @JsonProperty("TSOBOUTG")
    private List<NyttVedtakTillegg> boutgifter;

    @JsonProperty("TSODAGREIS")
    private List<NyttVedtakTillegg> dagligReise;

    @JsonProperty("TSOFLYTT")
    private List<NyttVedtakTillegg> flytting;

    @JsonProperty("TSOLMIDLER")
    private List<NyttVedtakTillegg> laeremidler;

    @JsonProperty("TSOREISAKT")
    private List<NyttVedtakTillegg> hjemreise;

    @JsonProperty("TSOREISOBL")
    private List<NyttVedtakTillegg> reiseObligatoriskSamling;

    @JsonProperty("TSOTILBARN")
    private List<NyttVedtakTillegg> tilsynBarn;

    @JsonProperty("TSOTILFAM")
    private List<NyttVedtakTillegg> tilsynFamiliemedlemmer;

    @JsonProperty("TSRBOUTG")
    private List<NyttVedtakTillegg> boutgifterArbeidssoekere;

    @JsonProperty("TSRDAGREIS")
    private List<NyttVedtakTillegg> dagligReiseArbeidssoekere;

    @JsonProperty("TSRFLYTT")
    private List<NyttVedtakTillegg> flyttingArbeidssoekere;

    @JsonProperty("TSRLMIDLER")
    private List<NyttVedtakTillegg> laeremidlerArbeidssoekere;

    @JsonProperty("TSRREISAKT")
    private List<NyttVedtakTillegg> hjemreiseArbeidssoekere;

    @JsonProperty("TSRREISARB")
    private List<NyttVedtakTillegg> reisestoenadArbeidssoekere;

    @JsonProperty("TSRREISOBL")
    private List<NyttVedtakTillegg> reiseObligatoriskSamlingArbeidssoekere;

    @JsonProperty("TSRTILBARN")
    private List<NyttVedtakTillegg> tilsynBarnArbeidssoekere;

    @JsonProperty("TSRTILFAM")
    private List<NyttVedtakTillegg> tilsynFamiliemedlemmerArbeidssoekere;
}
