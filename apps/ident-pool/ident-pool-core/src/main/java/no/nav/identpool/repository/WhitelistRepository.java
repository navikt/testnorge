package no.nav.identpool.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.identpool.domain.postgres.Whitelist;

@Repository
public interface WhitelistRepository extends CrudRepository<Whitelist, Long> {

    List<Whitelist> findAllBy();
}
