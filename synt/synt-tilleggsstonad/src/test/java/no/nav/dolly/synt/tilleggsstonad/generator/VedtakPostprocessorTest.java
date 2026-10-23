package no.nav.dolly.synt.tilleggsstonad.generator;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

import static org.assertj.core.api.Assertions.assertThat;

class VedtakPostprocessorTest {

    @Test
    void shouldOnlyLetFirstFulfilledVilkaarInGroupKeepStatusJ() {

        var vedtak = vedtak("JA");
        vedtak.put("TSTDROSJE", "J");
        vedtak.put("TSTOFFTRAN", "J");
        vedtak.put("TSTEGENBIL", "J");
        vedtak.put("TSTIKKELON", "J");

        var structured = VedtakPostprocessor.structure(vedtak, TilleggsstonadType.REISESTONAD_TIL_ARBEIDSSOKERE, new SplittableRandom(1));

        assertThat(structured.get("VILKAAR")).isEqualTo(List.of(
                Map.of("KODE", "TSTIKKELON", "STATUS", "J"),
                Map.of("KODE", "TSTOFFTRAN", "STATUS", "J"),
                Map.of("KODE", "TSTEGENBIL", "STATUS", "N"),
                Map.of("KODE", "TSTDROSJE", "STATUS", "N")));

    }

    @Test
    void shouldKeepVilkaarStatusWhenUtfallIsNei() {

        var vedtak = vedtak("NEI");
        vedtak.put("TSTOFFTRAN", "J");
        vedtak.put("TSTEGENBIL", "J");

        var structured = VedtakPostprocessor.structure(vedtak, TilleggsstonadType.REISESTONAD_TIL_ARBEIDSSOKERE, new SplittableRandom(1));

        assertThat(structured.get("VILKAAR")).isEqualTo(List.of(
                Map.of("KODE", "TSTOFFTRAN", "STATUS", "J"),
                Map.of("KODE", "TSTEGENBIL", "STATUS", "J")));

    }

    @Test
    void shouldAddLegalAvbruddskodeAndReplaceMissingValues() {

        var vedtak = vedtak("JA");
        vedtak.put("VEDTAKSTATUS", "INNST");
        vedtak.put("MAALGRUPPEKODE", null);

        var structured = VedtakPostprocessor.structure(vedtak, TilleggsstonadType.REISESTONAD_TIL_ARBEIDSSOKERE, new SplittableRandom(1));

        assertThat(structured).doesNotContainKey("VEDTAKSTATUS").containsEntry("MAALGRUPPEKODE", "");
        assertThat(structured.get("AVBRUDDSKODE")).isIn("EFOV", "FFAV");
        assertThat(structured.get("VEDTAKSPERIODE")).isEqualTo(Map.of("FOM", "2025-01-01", "TOM", ""));
        assertThat(List.copyOf(structured.keySet())).isSorted();

    }

    private static Map<String, Object> vedtak(String utfall) {

        var vedtak = new HashMap<String, Object>();
        vedtak.put("VEDTAKTYPE", "O");
        vedtak.put("VEDTAKSTATUS", "AVSLU");
        vedtak.put("UTFALL", utfall);
        vedtak.put("MAALGRUPPEKODE", "NEDSARBEVN");
        vedtak.put("FRA_DATO", "2025-01-01");
        vedtak.put("TIL_DATO", null);
        TilleggsstonadType.REISESTONAD_TIL_ARBEIDSSOKERE.getValues().forEach(kode -> vedtak.put(kode, "x"));
        return vedtak;

    }

}
