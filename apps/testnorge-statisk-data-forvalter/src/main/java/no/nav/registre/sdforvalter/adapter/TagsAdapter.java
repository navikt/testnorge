package no.nav.registre.sdforvalter.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.sdforvalter.database.model.TagModel;
import no.nav.registre.sdforvalter.database.repository.TagRepository;

@Component
@RequiredArgsConstructor
public class TagsAdapter {
    private final TagRepository repository;

    public TagModel save(String tag) {
        return repository.findById(tag)
                .orElseGet(() -> repository.save(TagModel.builder().tag(tag).build()));
    }
}
