package no.nav.registre.testnav.statistikkservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkType;
import no.nav.registre.testnav.statistikkservice.repository.model.StatistikkModel;

public interface StatistikkRepository extends JpaRepository<StatistikkModel, StatistikkType> {
}