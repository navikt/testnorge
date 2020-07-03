package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BegrunnelseIngenEllerRedusertUtbetalingKodeListe implements AltinnEnum {
    LOVLIG_FRAVAER("LovligFravaer"),
    FRAVAER_UTEN_GYLDIG_GRUNN("FravaerUtenGyldigGrunn"),
    ARBEID_OPPHOERT("ArbeidOpphoert"),
    BESKJED_GITT_FOR_SENT("BeskjedGittForSent"),
    MANGLER_OPPTJENING("ManglerOpptjening"),
    IKKE_LOENN("IkkeLoenn"),
    BETVILER_ARBEIDSUFOERHET("BetvilerArbeidsufoerhet"),
    IKKE_FRAVAER("IkkeFravaer"),
    STREIK_ELLER_LOCKOUT("StreikEllerLockout"),
    PERMITTERING("Permittering"),
    FISKER_MED_HYRE("FiskerMedHyre"),
    SAERREGLER("Saerregler");

    private String value;

    BegrunnelseIngenEllerRedusertUtbetalingKodeListe (String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
