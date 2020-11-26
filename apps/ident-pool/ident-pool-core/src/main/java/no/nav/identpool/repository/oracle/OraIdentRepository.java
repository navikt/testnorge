package no.nav.identpool.repository.oracle;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import no.nav.identpool.domain.oracle.OraIdent;

public interface OraIdentRepository extends PagingAndSortingRepository<OraIdent, Long> {

    Page<OraIdent> findAllByOrderByIdentity(Pageable pageable);
}
