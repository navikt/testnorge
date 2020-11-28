package no.nav.identpool.repository.oracle;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import no.nav.identpool.domain.oracle.OraAjourhold;

public interface OraAjourholdRepository extends JpaRepository<OraAjourhold, Long> {

    Page<OraAjourhold> findAllByOrderByIdentity(Pageable pageable);
}
