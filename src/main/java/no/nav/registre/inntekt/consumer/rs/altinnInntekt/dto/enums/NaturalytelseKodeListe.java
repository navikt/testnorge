package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NaturalytelseKodeListe implements AltinnEnum {
    AKSJER_GRUNNFONDSBEVIS_TIL_UNDERKURS("aksjerGrunnfondsbevisTilUnderkurs"),
    LOSJI("losji"),
    KOST_DOEGN("kostDoegn"),
    BESOEKSREISER_HJEMMET_ANNET("besoeksreiserHjemmetAnnet"),
    KOSTBESPARELSE_I_HJEMMET("kostbesparelseIHjemmet"),
    RENTEFORDEL_LAAN("rentefordelLaan"),
    BIL("bil"),
    KOST_DAGER("kostDager"),
    BOLIG("bolig"),
    SKATTEPLIKTIG_DEL_FORSIKRINGER("skattepliktigDelForsikringer"),
    FRI_TRANSPORT("friTransport"),
    OPSJONER("opsjoner"),
    TILSKUDD_BARNEHAGEPLASS("tilskuddBarnehageplass"),
    ANNET("annet"),
    BEDRIFTSBARNEHAGEPLASS("bedriftsbarnehageplass"),
    YRKEBIL_TJENESTLIGBEHOV_KILOMETER("yrkebilTjenestligbehovKilometer"),
    YRKEBIL_TJENESTLIGBEHOV_LISTEPRIS("yrkebilTjenestligbehovListepris"),
    INNBETALING_TIL_UTENLANDSK_PENSJONSORDNING("innbetalingTilUtenlandskPensjonsordning"),
    ELEKTRONISK_KOMMUNIKASJON("elektroniskKommunikasjon");

    private String value;

    NaturalytelseKodeListe (String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
