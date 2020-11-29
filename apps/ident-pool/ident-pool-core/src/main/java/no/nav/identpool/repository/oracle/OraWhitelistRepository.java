package no.nav.identpool.repository.oracle;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.identpool.domain.oracle.OraWhitelist;

@Repository
public interface OraWhitelistRepository extends CrudRepository<OraWhitelist, Long> {

}
