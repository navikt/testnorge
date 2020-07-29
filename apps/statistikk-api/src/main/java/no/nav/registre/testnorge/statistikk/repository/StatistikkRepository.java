package no.nav.registre.testnorge.statistikk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import no.nav.registre.testnorge.dto.statistikk.v1.StatistikkType;
import no.nav.registre.testnorge.statistikk.repository.model.StatistikkModel;

public interface StatistikkRepository extends JpaRepository<StatistikkModel, StatistikkType> {
}