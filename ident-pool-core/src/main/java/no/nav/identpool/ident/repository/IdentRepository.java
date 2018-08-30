package no.nav.identpool.ident.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface IdentRepository extends CrudRepository<IdentEntity, Long> {
    IdentEntity findTopByPersonidentifikator(String personidentifkator);

    List<IdentEntity> findAllByRekvireringsstatus(String rekvireringsstatus);
    List<IdentEntity> findAllByRekvireringsstatusAndIdenttype(String rekvireringsstatus, String identtype);
}
