package no.nav.dolly.repository.oracle;

import org.springframework.data.repository.PagingAndSortingRepository;

import no.nav.dolly.domain.jpa.oracle.OraBruker;

public interface OraBrukerRepository extends PagingAndSortingRepository<OraBruker, Long> {

}