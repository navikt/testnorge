package no.nav.repository;

import no.nav.jpa.Ident;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface IdentRepository extends Repository<Ident, Long>{

    List<Ident> findAll();

    List<Ident> findById();
}
