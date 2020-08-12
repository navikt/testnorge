package no.nav.registre.testnorge.rapportering.adapter;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.testnorge.rapportering.domain.Report;
import no.nav.registre.testnorge.rapportering.repository.ReportRepository;

@Component
@RequiredArgsConstructor
public class ReportAdapter {
    private final ReportRepository reportRepository;

    public void save(Report report) {
        reportRepository.save(report.toModel());
    }

    public List<Report> findAll() {
        return StreamSupport.stream(reportRepository.findAll().spliterator(), false)
                .map(Report::new)
                .collect(Collectors.toList());
    }

    public void delete(Long reportId) {
        reportRepository.deleteById(reportId);
    }

    public void deleteAll() {
        reportRepository.deleteAll();
    }
}
