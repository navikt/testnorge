package no.nav.registre.hodejegeren.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SyntHistorikkRepository extends MongoRepository<SyntHistorikk, String> {

    @Query("select sh.kilder from syntHistorikk sh where sh.navnPaaKilde = navnPaaKilde")
    Kilde findKildeByName(String navnPaaKilde);
}
