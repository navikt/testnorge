package no.nav.dolly.repository;

import org.springframework.data.repository.Repository;

import no.nav.dolly.domain.jpa.BrukerFavoritter;

public interface BrukerFavoritterRepository extends Repository<BrukerFavoritter, Long> {

    BrukerFavoritter save(BrukerFavoritter brukerFavoritter);
}