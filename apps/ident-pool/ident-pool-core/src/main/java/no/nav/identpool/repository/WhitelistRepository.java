package no.nav.identpool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.identpool.domain.Whitelist;

@Repository
public interface WhitelistRepository extends CrudRepository<Whitelist, Long> {

}
