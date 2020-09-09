package no.nav.registre.testnorge.hendelse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

import no.nav.registre.testnorge.libs.dto.hendelse.v1.HendelseType;
import no.nav.registre.testnorge.hendelse.repository.model.HendelseModel;

public interface HendelseRepository extends JpaRepository<HendelseModel, Long> {
    List<HendelseModel> findByIdent(String ident);

    List<HendelseModel> findByIdentAndHendelse(String ident, HendelseType hendelse);

    List<HendelseModel> findByHendelse(HendelseType hendelse);

    @Query(value = "from HendelseModel h where (h.tom is null and ?2 >= h.fom or h.tom is not null and ?2 BETWEEN h.fom AND h.tom) and h.ident = ?1")
    List<HendelseModel> findBy(String ident, Date betweenDate);

    @Query(value = "from HendelseModel h where h.tom is null and ?1 >= h.fom or h.tom is not null and ?1 BETWEEN h.fom AND h.tom")
    List<HendelseModel> findBy(Date betweenDate);

    @Query(value = "from HendelseModel h where (h.tom is null and ?2 >= h.fom or h.tom is not null and ?2 BETWEEN h.fom AND h.tom) and h.ident = ?1 and h.hendelse = ?3")
    List<HendelseModel> findBy(String ident, Date betweenDate, HendelseType hendelse);

    @Query(value = "from HendelseModel h where (h.tom is null and ?1 >= h.fom or h.tom is not null and ?1 BETWEEN h.fom AND h.tom) and h.hendelse = ?2")
    List<HendelseModel> findBy(Date betweenDate, HendelseType hendelse);
}