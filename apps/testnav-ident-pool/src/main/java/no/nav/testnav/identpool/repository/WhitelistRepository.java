package no.nav.testnav.identpool.repository;

import no.nav.testnav.identpool.domain.Whitelist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhitelistRepository extends CrudRepository<Whitelist, Long> {

}
