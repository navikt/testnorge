package no.nav.testnav.libs.dto.oppdragservice.v1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OppdragKodeverk {

        JA_NEI (Oppdrag.JaNei.class),
        KODE_STATUS_LINJE(Oppdrag.KodeStatusLinje.class),
        FRADRAG_TILLEGG(Oppdrag.FradragTillegg.class),
        KODE_ARBEIDSGIVER(Oppdrag.KodeArbeidsgiver.class),
        KODE_STATUS(Oppdrag.KodeStatus.class),
        KODE_ENDRING(Oppdrag.KodeEndring.class),
        KODE_ENDRING_TYPE(Oppdrag.KodeEndringType.class),
        SATS_TYPE(Oppdrag.SatsType.class),
        UTBETALING_FREKVENS_TYPE(Oppdrag.UtbetalingFrekvensType.class),
        VALUTA_TYPE(Oppdrag.ValutaType.class);

    private final Class implementasjon;

    }