package no.nav.dolly.synt.dagpenger.model;

import no.nav.dolly.synt.dagpenger.dto.DagpengevedtakDto;

import java.util.List;

public interface ModelService {

	List<DagpengevedtakDto> generateVedtak(String rettighet, List<String> startDates);
}
