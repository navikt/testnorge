package no.nav.registre.testnorge.hendelse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import no.nav.registre.testnorge.hendelse.repository.model.HendelseModel;

public interface HendelseRepository extends JpaRepository<HendelseModel, Long> {
    List<HendelseModel> findByIdentId(Long ident_id);
}