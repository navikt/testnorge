package no.nav.dolly.synt.aap.onnx;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.aap.SyntAapApplication;
import no.nav.dolly.synt.aap.bucket.BucketModels;
import no.nav.dolly.synt.aap.dto.AapVedtakDto;
import no.nav.dolly.synt.aap.dto.AatforAaunguforFriMkVedtakDto;
import no.nav.dolly.synt.aap.dto.Vedtak115Dto;
import no.nav.dolly.synt.aap.dto.VedtakRequestDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("prod")
@Slf4j
@RequiredArgsConstructor
class OnnxBucketService implements OnnxService {

    private final SyntAapApplication.Config config;
    private AapVedtakGenerator generator;

    @PostConstruct
    void postConstruct()
            throws Exception {

        var modelDirectory = BucketModels.get(config.getBucket(), config.getModels(), "synt-aap-models-");
        this.generator = new AapVedtakGenerator(modelDirectory);
        log.info("Successfully initialized ONNX models from bucket {}", config.getBucket());

    }

    @Override
    public List<AapVedtakDto> generateAap(List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        return generator
                .generateVedtak(AapModelType.AAP, requests, brukInnsendtTilDato)
                .stream()
                .map(AapVedtakMapper::toAapVedtak)
                .toList();
    }

    @Override
    public List<AapVedtakDto> generateFilteredAap(List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        var filteredRequests = AapHistoryFilter.removeIllogicalRequests(requests);
        var generated = generateAap(filteredRequests, brukInnsendtTilDato);
        return AapHistoryFilter.postprocessForUseInHistory(generated);
    }

    @Override
    public List<Vedtak115Dto> generate115(List<VedtakRequestDto> requests) {
        return generator
                .generateVedtak(AapModelType.AAP_115, requests, true)
                .stream()
                .map(AapVedtakMapper::toVedtak115)
                .toList();
    }

    @Override
    public List<AatforAaunguforFriMkVedtakDto> generateFriMk(List<VedtakRequestDto> requests) {
        return generator
                .generateVedtak(AapModelType.FRI_MK, requests, true)
                .stream()
                .map(AapVedtakMapper::toAatforAaunguforFriMkVedtak)
                .toList();
    }

    @Override
    public List<AatforAaunguforFriMkVedtakDto> generateAaungufor(List<VedtakRequestDto> requests) {
        return generator
                .generateVedtak(AapModelType.AAUNGUFOR, requests, true)
                .stream()
                .map(AapVedtakMapper::toAatforAaunguforFriMkVedtak)
                .toList();
    }

    @Override
    public List<AatforAaunguforFriMkVedtakDto> generateAatfor(List<VedtakRequestDto> requests) {
        return generator
                .generateVedtak(AapModelType.AATFOR, requests, true)
                .stream()
                .map(AapVedtakMapper::toAatforAaunguforFriMkVedtak)
                .toList();
    }

}
