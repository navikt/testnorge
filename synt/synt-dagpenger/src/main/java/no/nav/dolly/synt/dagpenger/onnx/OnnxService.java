package no.nav.dolly.synt.dagpenger.onnx;

import no.nav.dolly.synt.dagpenger.dto.DagpengevedtakDto;

import java.util.List;

public interface OnnxService {

    List<DagpengevedtakDto> generateVedtak(String rettighet, List<String> startDates);

}
