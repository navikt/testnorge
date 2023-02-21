package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BrukerFavoritter;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface BrukerFavoritterRepository extends Repository<BrukerFavoritter, Long> {

    BrukerFavoritter save(BrukerFavoritter brukerFavoritter);

    @Modifying
    @Query(value = "update bruker_favoritter b set b.bruker_id = :newBruker " +
            "where b.bruker_id = :oldBruker", nativeQuery = true)
    void updateBrukerFavoritter(@Param("oldBruker") Long oldBruker,
                                @Param("newBruker") Long newBruker);
}