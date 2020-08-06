package no.nav.registre.testnorge.rapportering.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.testnorge.rapportering.repository.model.ReportModel;

public interface ReportRepository extends CrudRepository<ReportModel, Long> {
}
