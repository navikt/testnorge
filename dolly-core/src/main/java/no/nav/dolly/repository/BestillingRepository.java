package no.nav.dolly.repository;

import no.nav.jpa.Bestilling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface BestillingRepository extends JpaRepository<Bestilling, Long> {


}
