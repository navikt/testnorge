package no.nav.identpool.repository;

import no.nav.identpool.domain.Whitelist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhitelistRepository extends CrudRepository<Whitelist, Long> {

}
