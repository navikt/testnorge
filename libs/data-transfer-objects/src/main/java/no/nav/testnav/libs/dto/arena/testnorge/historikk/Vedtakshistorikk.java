package no.nav.testnav.libs.dto.arena.testnorge.historikk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
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

    @JsonProperty("FRI_MK_AAP")
    private List<NyttVedtakAap> fritakMeldekort;

    @JsonProperty("BASI")
    private List<NyttVedtakTiltak> tiltakspenger;

    @JsonProperty("BTIL")
    private List<NyttVedtakTiltak> barnetillegg;

    @JsonProperty("TILTAK")
    private List<NyttVedtakTiltak> tiltaksdeltakelse;

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

    public List<NyttVedtak> getAlleVedtak() {
        List<NyttVedtak> alleVedtak = new ArrayList<>();
        alleVedtak.addAll(getAlleAapVedtak());
        alleVedtak.addAll(getAlleTiltakVedtak());
        alleVedtak.addAll(getAlleTilleggVedtak());

        return alleVedtak;
    }

    public List<NyttVedtakAap> getAlleAapVedtak() {
        List<NyttVedtakAap> alleAapVedtak = new ArrayList<>();
        addAllIfNotNull(alleAapVedtak, aap);
        addAllIfNotNull(alleAapVedtak, aap115);
        addAllIfNotNull(alleAapVedtak, ungUfoer);
        addAllIfNotNull(alleAapVedtak, tvungenForvaltning);
        addAllIfNotNull(alleAapVedtak, fritakMeldekort);

        return alleAapVedtak;
    }

    public List<NyttVedtakTiltak> getAlleTiltakVedtak() {
        List<NyttVedtakTiltak> alleTiltakVedtak = new ArrayList<>();
        addAllIfNotNull(alleTiltakVedtak, tiltakspenger);
        addAllIfNotNull(alleTiltakVedtak, barnetillegg);
        addAllIfNotNull(alleTiltakVedtak, tiltaksdeltakelse);

        return alleTiltakVedtak;
    }

    public List<NyttVedtakTillegg> getAlleTilleggVedtak() {
        List<NyttVedtakTillegg> alleTilleggVedtak = new ArrayList<>();
        addAllIfNotNull(alleTilleggVedtak, boutgifter);
        addAllIfNotNull(alleTilleggVedtak, dagligReise);
        addAllIfNotNull(alleTilleggVedtak, flytting);
        addAllIfNotNull(alleTilleggVedtak, laeremidler);
        addAllIfNotNull(alleTilleggVedtak, hjemreise);
        addAllIfNotNull(alleTilleggVedtak, reiseObligatoriskSamling);
        addAllIfNotNull(alleTilleggVedtak, tilsynBarn);
        addAllIfNotNull(alleTilleggVedtak, tilsynFamiliemedlemmer);
        addAllIfNotNull(alleTilleggVedtak, boutgifterArbeidssoekere);
        addAllIfNotNull(alleTilleggVedtak, dagligReiseArbeidssoekere);
        addAllIfNotNull(alleTilleggVedtak, flyttingArbeidssoekere);
        addAllIfNotNull(alleTilleggVedtak, laeremidlerArbeidssoekere);
        addAllIfNotNull(alleTilleggVedtak, hjemreiseArbeidssoekere);
        addAllIfNotNull(alleTilleggVedtak, reisestoenadArbeidssoekere);
        addAllIfNotNull(alleTilleggVedtak, reiseObligatoriskSamlingArbeidssoekere);
        addAllIfNotNull(alleTilleggVedtak, tilsynBarnArbeidssoekere);
        addAllIfNotNull(alleTilleggVedtak, tilsynFamiliemedlemmerArbeidssoekere);

        return alleTilleggVedtak;
    }

    private static <E> void addAllIfNotNull(
            List<E> list,
            Collection<? extends E> c
    ) {
        if (c != null) {
            list.addAll(c);
        }
    }
}
