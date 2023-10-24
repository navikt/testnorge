package no.nav.registre.sdforvalter.adapter;

import lombok.RequiredArgsConstructor;
import no.nav.registre.sdforvalter.database.model.EregTagModel;
import no.nav.registre.sdforvalter.database.model.TagModel;
import no.nav.registre.sdforvalter.database.repository.EregTagRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EregTagAdapter {
    private final EregTagRepository repository;

    public EregTagModel save(EregTagModel model) {
        return repository
                .findBy(model.getEreg().getOrgnr(), model.getTag().getTag())
                .orElseGet(() -> repository.save(model));
    }

    public List<TagModel> findAllTagsBy(String orgnr) {
        return repository
                .findAllBy(orgnr)
                .stream()
                .map(EregTagModel::getTag)
                .toList();
    }
}
