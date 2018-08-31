package no.nav.identpool.ident.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;

public interface IdentRepository extends JpaRepository<IdentEntity, Long> {
    IdentEntity findTopByPersonidentifikator(String personidentifkator);

    List<IdentEntity> findByRekvireringsstatus(Rekvireringsstatus rekvireringsstatus, Pageable pageable);

    List<IdentEntity> findByRekvireringsstatusAndIdenttype(Rekvireringsstatus rekvireringsstatus, Identtype identtype, Pageable pageable);
}
