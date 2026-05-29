package no.nav.dolly.synt.aap.onnx;

import no.nav.dolly.synt.aap.dto.AapVedtakDto;
import no.nav.dolly.synt.aap.dto.AatforAaunguforFriMkVedtakDto;
import no.nav.dolly.synt.aap.dto.Vedtak115Dto;
import no.nav.dolly.synt.aap.dto.VedtakRequestDto;

import java.util.List;

public interface OnnxService {

    List<AapVedtakDto> generateAap(List<VedtakRequestDto> requests, boolean brukInnsendtTilDato);

    List<AapVedtakDto> generateFilteredAap(List<VedtakRequestDto> requests, boolean brukInnsendtTilDato);

    List<Vedtak115Dto> generate115(List<VedtakRequestDto> requests);

    List<AatforAaunguforFriMkVedtakDto> generateFriMk(List<VedtakRequestDto> requests);

    List<AatforAaunguforFriMkVedtakDto> generateAaungufor(List<VedtakRequestDto> requests);

    List<AatforAaunguforFriMkVedtakDto> generateAatfor(List<VedtakRequestDto> requests);
}
