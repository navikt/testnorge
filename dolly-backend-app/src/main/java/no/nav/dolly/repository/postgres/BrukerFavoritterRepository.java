package no.nav.dolly.repository.postgres;

import org.springframework.data.repository.Repository;

import no.nav.dolly.domain.jpa.postgres.BrukerFavoritter;

public interface BrukerFavoritterRepository extends Repository<BrukerFavoritter, Long> {

    BrukerFavoritter save(BrukerFavoritter brukerFavoritter);
}