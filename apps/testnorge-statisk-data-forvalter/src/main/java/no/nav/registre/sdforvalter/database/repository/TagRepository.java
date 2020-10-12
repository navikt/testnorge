package no.nav.registre.sdforvalter.database.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.sdforvalter.database.model.TagModel;

public interface TagRepository extends CrudRepository<TagModel, String> {
}
