package no.nav.dolly.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

import no.nav.dolly.domain.jpa.TransaksjonMapping;

public interface TransaksjonMappingRepository extends CrudRepository<TransaksjonMapping, Long> {

    Optional<List<TransaksjonMapping>> findAllByIdent(String ident);

    Optional<List<TransaksjonMapping>> findAllBySystemAndIdent(String system, String ident);
}