package no.nav.dolly.synt.dagpenger.onnx;

import no.nav.dolly.synt.dagpenger.dto.DagpengevedtakDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("test")
class OnnxTestService implements OnnxService {

    @Override
    public List<DagpengevedtakDto> generateVedtak(String rettighet, List<String> startDates) {
        return List.of();
    }

}
