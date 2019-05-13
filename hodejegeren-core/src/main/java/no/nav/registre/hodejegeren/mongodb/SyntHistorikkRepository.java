package no.nav.registre.hodejegeren.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyntHistorikkRepository extends MongoRepository<SyntHistorikk, String> {

}
