package no.nav.dolly.synt.aap.onnx;

import no.nav.dolly.synt.aap.dto.AapVedtakDto;
import no.nav.dolly.synt.aap.dto.AatforAaunguforFriMkVedtakDto;
import no.nav.dolly.synt.aap.dto.Vedtak115Dto;
import no.nav.dolly.synt.aap.dto.VedtakRequestDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("test")
class OnnxTestService implements OnnxService {

    @Override
    public List<AapVedtakDto> generateAap(List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        return List.of();
    }

    @Override
    public List<AapVedtakDto> generateFilteredAap(List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        return List.of();
    }

    @Override
    public List<Vedtak115Dto> generate115(List<VedtakRequestDto> requests) {
        return List.of();
    }

    @Override
    public List<AatforAaunguforFriMkVedtakDto> generateFriMk(List<VedtakRequestDto> requests) {
        return List.of();
    }

    @Override
    public List<AatforAaunguforFriMkVedtakDto> generateAaungufor(List<VedtakRequestDto> requests) {
        return List.of();
    }

    @Override
    public List<AatforAaunguforFriMkVedtakDto> generateAatfor(List<VedtakRequestDto> requests) {
        return List.of();
    }
}
