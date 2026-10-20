package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import java.util.List;

public record PensjonsavtaleRequest(
        String ident,
        String produktBetegnelse,
        AvtaleKategori avtaleKategori,
        List<Utbetalingsperiode> utbetalingsperioder,
        List<String> miljoer
) {

    public record Utbetalingsperiode(
            Integer startAlderAar,
            Integer startAlderMaaned,
            Integer sluttAlderAar,
            Integer sluttAlderMaaned,
            Integer aarligUtbetaling
    ) {
    }

    public enum AvtaleKategori {
        NONE,
        UNKNOWN,
        INDIVIDUELL_ORDNING,
        PRIVAT_AFP,
        PRIVAT_TJENESTEPENSJON,
        OFFENTLIG_TJENESTEPENSJON,
        FOLKETRYGD
    }
}
