package no.nav.dolly.domain.resultset.inntektsmeldingstub;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum NaturalytelseType {

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

    @JsonValue
    private final String jsonValue;

}
