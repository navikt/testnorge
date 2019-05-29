package no.nav.registre.hodejegeren.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyntHistorikkRepository extends MongoRepository<SyntHistorikk, String> {

    @Query(value = "{'kilder.navn' : ?0}")
    List<SyntHistorikk> findAllByKildenavn(String kilde);

    @Query(value = "{'kilder.navn' : ?0}", fields = "{'_id':1}")
    List<SyntHistorikk> findAllIdsByKildenavn(String kilde);
}
