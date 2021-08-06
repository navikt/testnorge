package no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter;

import lombok.Getter;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.AldersUfoereEtterlatteAvtalefestetOgKrigspensjon;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.BilOgBaat;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.BonusFraForsvaret;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.DagmammaIEgenBolig;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.Inntjeningsforhold;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.Livrente;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.LottOgPartInnenFiske;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.Nettoloennsordning;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.NorskKontinentalsokkel;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.Periode;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.ReiseKostOgLosji;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.UtenlandskArtist;

@Getter
public class Tilleggsinformasjon {

    private BilOgBaat bilOgBaat;
    private DagmammaIEgenBolig dagmammaIEgenBolig;
    private Periode etterbetalingsperiode;
    private NorskKontinentalsokkel inntektPaaNorskKontinentalsokkel;
    private Inntjeningsforhold inntjeningsforhold;
    private Livrente livrente;
    private LottOgPartInnenFiske lottOgPart;
    private Nettoloennsordning nettoloenn;
    private AldersUfoereEtterlatteAvtalefestetOgKrigspensjon pensjon;
    private ReiseKostOgLosji reiseKostOgLosji;
    private UtenlandskArtist utenlandskArtist;
    private BonusFraForsvaret bonusFraForsvaret;

    private Tilleggsinformasjon() {
    }

    public Tilleggsinformasjon(BilOgBaat bilOgBaat) {
        this.bilOgBaat = bilOgBaat;
    }

    public Tilleggsinformasjon(DagmammaIEgenBolig dagmammaIEgenBolig) {
        this.dagmammaIEgenBolig = dagmammaIEgenBolig;
    }

    public Tilleggsinformasjon(Periode etterbetalingsperiode) {
        this.etterbetalingsperiode = etterbetalingsperiode;
    }

    public Tilleggsinformasjon(NorskKontinentalsokkel inntektPaaNorskKontinentalsokkel) {
        this.inntektPaaNorskKontinentalsokkel = inntektPaaNorskKontinentalsokkel;
    }

    public Tilleggsinformasjon(Inntjeningsforhold inntjeningsforhold) {
        this.inntjeningsforhold = inntjeningsforhold;
    }

    public Tilleggsinformasjon(Livrente livrente) {
        this.livrente = livrente;
    }

    public Tilleggsinformasjon(LottOgPartInnenFiske lottOgPart) {
        this.lottOgPart = lottOgPart;
    }

    public Tilleggsinformasjon(Nettoloennsordning nettoloenn) {
        this.nettoloenn = nettoloenn;
    }

    public Tilleggsinformasjon(AldersUfoereEtterlatteAvtalefestetOgKrigspensjon pensjon) {
        this.pensjon = pensjon;
    }

    public Tilleggsinformasjon(ReiseKostOgLosji reiseKostOgLosji) {
        this.reiseKostOgLosji = reiseKostOgLosji;
    }

    public Tilleggsinformasjon(UtenlandskArtist utenlandskArtist) {
        this.utenlandskArtist = utenlandskArtist;
    }

    public Tilleggsinformasjon(BonusFraForsvaret bonusFraForsvaret) {
        this.bonusFraForsvaret = bonusFraForsvaret;
    }
}
