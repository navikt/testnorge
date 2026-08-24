package no.nav.dolly.synt.tilleggsstonad.onnx;

import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile("local")
class OnnxLocalService implements OnnxService {

    @Override
    public List<Map<String, Object>> generateVedtak(TilleggsstonadType tilleggsstonadType, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        return requests.stream()
                .map(request -> toVedtak(tilleggsstonadType, request, brukInnsendtTilDato))
                .toList();
    }

    private static Map<String, Object> toVedtak(TilleggsstonadType tilleggsstonadType, VedtakRequestDto request, boolean brukInnsendtTilDato) {
        var vedtak = new LinkedHashMap<String, Object>();
        var vedtaksperiode = new LinkedHashMap<String, Object>();
        vedtaksperiode.put("FOM", request.getFraDato());
        vedtaksperiode.put("TOM", brukInnsendtTilDato ? request.getTilDato() : request.getFraDato());

        vedtak.put("AKTIVITETSTATUSKODE", "AKTIV");
        vedtak.put("DATO_MOTTATT", request.getVedtakDato());
        vedtak.put("MAALGRUPPEKODE", "STD");
        vedtak.put("RETTIGHET_KODE", tilleggsstonadType.name());
        vedtak.put("UTFALL", request.getUtfall());
        vedtak.put("VEDTAKSPERIODE", vedtaksperiode);
        vedtak.put("VEDKTAKTYPE", request.getVedtakTypeKode());
        vedtak.put("VILKAAR", List.of());
        vedtak.put(tilleggsstonadType.resultField(), List.of());
        return vedtak;
    }

}
