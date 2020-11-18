package no.nav.dolly.repository.oracle;

import org.springframework.data.repository.PagingAndSortingRepository;

import no.nav.dolly.domain.jpa.oracle.OraTestident;

public interface OraIdentRepository extends PagingAndSortingRepository<OraTestident, String> {

}
