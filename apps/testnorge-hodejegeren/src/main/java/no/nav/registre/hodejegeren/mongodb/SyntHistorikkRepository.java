package no.nav.registre.hodejegeren.mongodb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyntHistorikkRepository extends MongoRepository<SyntHistorikk, String> {

    @Query(value = "{'kilder.navn' : ?0}", fields = "{'_id':1}")
    List<SyntHistorikk> findAllIdsByKildenavn(String kilde);

    List<SyntHistorikk> findAllByIdIn(List<String> ids);

    Page<SyntHistorikk> findAllByIdIn(
            List<String> ids,
            Pageable pageable
    );
}
