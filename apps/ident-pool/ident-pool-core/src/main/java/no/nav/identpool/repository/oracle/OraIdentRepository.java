package no.nav.identpool.repository.oracle;

import org.springframework.data.repository.PagingAndSortingRepository;

import no.nav.identpool.domain.oracle.OraIdent;

public interface OraIdentRepository extends PagingAndSortingRepository<OraIdent, Long> {

}
