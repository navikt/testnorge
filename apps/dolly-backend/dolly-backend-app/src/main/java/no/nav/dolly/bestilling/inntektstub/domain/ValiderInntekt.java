package no.nav.dolly.bestilling.inntektstub.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValiderInntekt {

    enum Inntektstype {LOENNSINNTEKT, YTELSE_FRA_OFFENTLIGE, PENSJON_ELLER_TRYGD, NAERINGSINNTEKT}

    private String aaretUtbetalingenGjelderFor;
    private String antall;
    private String beskrivelse;
    private String etterbetalingsperiodeSlutt;
    private String etterbetalingsperiodeStart;
    private String fordel;
    private String grunnpensjonsbeloep;
    private String heravEtterlattepensjon;
    private Boolean inngaarIGrunnlagForTrekk;
    private Inntektstype inntektstype;
    private String inntjeningsforhold;
    private String opptjeningsland;
    private String pensjonTidsromSlutt;
    private String pensjonTidsromStart;
    private String pensjonsgrad;
    private String persontype;
    private String skatteOgAvgiftsregel;
    private String skattemessigBosattILand;
    private String tilleggsinformasjonstype;
    private String tilleggspensjonsbeloep;
    private String ufoeregrad;
    private Boolean utloeserArbeidsgiveravgift;
}