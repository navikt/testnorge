package no.nav.testnav.libs.dto.arena.testnorge.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.boutgift.Boutgift;
import no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.dagligreise.DagligReise;
import no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.flytting.Flytting;
import no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.hjemreise.Hjemreise;
import no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.laeremidler.Laeremiddel;
import no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.reiseobligatorisksamling.ReiseObligatoriskSamling;
import no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.reisestoenad.ReisestoenadArbeidssoeker;
import no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.tilsyn.TilsynBarn;
import no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.tilsyn.TilsynFamiliemedlemmer;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NyttVedtakTillegg extends NyttVedtak {

    @JsonAlias({ "RETTIGHET_KODE", "rettighetKode" })
    private String rettighetKode;

    @JsonAlias({ "UTFALL", "utfallKode" })
    private String utfallKode;

    @JsonIgnore
    @JsonAlias({ "AKTIVITETSTATUSKODE", "aktivitetstatuskode" })
    private String aktivitetstatuskode;

    @JsonAlias({ "MAALGRUPPEKODE", "maalgruppeKode" })
    private String maalgruppeKode;

    @JsonAlias({ "VEDTAKSPERIODE", "vedtaksperiode" })
    private Vedtaksperiode vedtaksperiode;

    @JsonAlias({ "BOUTGIFTER", "boutgifter" })
    private List<Boutgift> boutgifter;

    @JsonAlias({ "DAGLIG_REISE", "dagligReise" })
    private List<DagligReise> dagligReise;

    @JsonAlias({ "FLYTTING", "flytting" })
    private List<Flytting> flytting;

    @JsonAlias({ "LAEREMIDLER", "laeremidler" })
    private List<Laeremiddel> laeremidler;

    @JsonAlias({ "REISE_VED_OPPSTART_OG_AVSLUTNING_AV_AKTIVITET_OG_HJEMREISE", "reiseVedOppstartOgAvslutningAvAktivitetOgHjemreise" })
    private List<Hjemreise> reiseVedOppstartOgAvslutningAvAktivitetOgHjemreise;

    @JsonAlias({ "REISE_OBLIGATORISK_SAMLING", "reiseObligatoriskSamling" })
    private List<ReiseObligatoriskSamling> reiseObligatoriskSamling;

    @JsonAlias({ "TILSYN_BARN", "tilsynBarn" })
    private List<TilsynBarn> tilsynBarn;

    @JsonAlias({ "TILSYN_FAMILIEMEDLEMMER", "tilsynFamiliemedlemmer" })
    private List<TilsynFamiliemedlemmer> tilsynFamiliemedlemmer;

    @JsonAlias({ "REISESTONAD_ARBEIDSSOKER", "reisestonadArbeidssoker" })
    private List<ReisestoenadArbeidssoeker> reisestonadArbeidssoker;

    @JsonIgnore
    @Override
    public RettighetType getRettighetType(){
        return RettighetType.TILLEGG;
    }
}
