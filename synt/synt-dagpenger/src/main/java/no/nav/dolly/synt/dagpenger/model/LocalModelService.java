package no.nav.dolly.synt.dagpenger.model;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.synt.dagpenger.dto.DagpengevedtakDto;
import no.nav.dolly.synt.dagpenger.onnx.DagpengerGeneratorBean;
import no.nav.dolly.synt.dagpenger.onnx.RettighetType;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class LocalModelService implements ModelService {

    private final DagpengerGeneratorBean dagpengerGeneratorBean;

    @Override
    public List<DagpengevedtakDto> generateVedtak(String rettighet, List<String> startDates) {
        RettighetType rettighetType = RettighetType.valueOf(rettighet.toUpperCase(Locale.ROOT));
        return dagpengerGeneratorBean.generateVedtak(rettighetType, startDates).stream()
                .map(VedtakMapper::fromPrediction)
                .collect(Collectors.toList());
    }
}
