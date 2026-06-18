package no.nav.dolly.synt.dagpenger.onnx;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.dagpenger.dto.DagpengevedtakDto;
import no.nav.dolly.synt.dagpenger.models.LocalModels;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Profile("local")
@Slf4j
class OnnxLocalService implements OnnxService {

    private DagpengevedtakGenerator generator;

    @PostConstruct
    void postConstruct() {

        var modelDirectory = LocalModels.get();
        this.generator = new DagpengevedtakGenerator(modelDirectory);
        log.info("Successfully initialized ONNX models from folder {}", modelDirectory.toAbsolutePath());

    }

    @Override
    public List<DagpengevedtakDto> generateVedtak(String rettighet, List<String> startDates) {

        var rettighetType = RettighetType.valueOf(rettighet.toUpperCase(Locale.ROOT));
        return generator.generateVedtak(rettighetType, startDates)
                .stream()
                .map(VedtakMapper::fromPrediction)
                .collect(Collectors.toList());

    }

}
