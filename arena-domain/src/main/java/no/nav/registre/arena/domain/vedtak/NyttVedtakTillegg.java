package no.nav.registre.arena.domain.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.arena.domain.tilleggsstoenad.Vedtaksperiode;
import no.nav.registre.arena.domain.tilleggsstoenad.boutgift.Boutgift;
import no.nav.registre.arena.domain.tilleggsstoenad.flytting.Flytting;
import no.nav.registre.arena.domain.tilleggsstoenad.hjemreise.Hjemreise;
import no.nav.registre.arena.domain.tilleggsstoenad.laeremidler.Laeremiddel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NyttVedtakTillegg extends NyttVedtak {

    @JsonAlias({ "RETTIGHET_KODE", "rettighetKode" })
    private String rettighetKode;

    // fix:
    @JsonAlias({ "UTFALL", "utfallKode" })
    private String utfallKode;

    @JsonIgnore
    @JsonAlias({ "AKTIVITETSTATUSKODE", "aktivitetstatuskode" })
    private String aktivitetstatuskode;

    @JsonAlias({ "MAALGRUPPEKODE", "maalgruppeKode" })
    private String maalgruppeKode;

    @JsonAlias({ "VEDTAKSPERIODE", "vedtaksperiode" })
    private Vedtaksperiode vedtaksperiode;

    @JsonAlias({ "LAEREMIDLER", "laeremidler" })
    private List<Laeremiddel> laeremidler;

    @JsonAlias({ "BOUTGIFTER", "boutgifter" })
    private List<Boutgift> boutgifter;

    @JsonAlias({ "FLYTTING", "flytting" })
    private List<Flytting> flytting;

    @JsonAlias({ "REISE_VED_OPPSTART_OG_AVSLUTNING_AV_AKTIVITET_OG_HJEMREISE", "reiseVedOppstartOgAvslutningAvAktivitetOgHjemreise" })
    private List<Hjemreise> reiseVedOppstartOgAvslutningAvAktivitetOgHjemreise;
}
