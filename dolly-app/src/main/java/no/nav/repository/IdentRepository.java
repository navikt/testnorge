package no.nav.repository;

import no.nav.jpa.Testident;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface IdentRepository extends Repository<Testident, Long>{

    List<Testident> findAll();

    List<Testident> findById();
}
