package no.nav.registre.sdforvalter.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.database.model.TagModel;
import no.nav.registre.sdforvalter.database.model.TpsIdentTagModel;
import no.nav.registre.sdforvalter.database.repository.TpsIdentTagRepository;

@Component
@RequiredArgsConstructor
public class TpsIdentTagAdapter {
    private final TpsIdentTagRepository identTagRepository;

    public TpsIdentTagModel save(TpsIdentTagModel model) {
        return identTagRepository
                .findBy(model.getIdent().getFnr(), model.getTag().getTag())
                .orElseGet(() -> identTagRepository.save(model));
    }

    public List<TagModel> findAllTagsByIdent(String ident) {
        return identTagRepository
                .findAllByIdent(ident)
                .stream()
                .map(TpsIdentTagModel::getTag)
                .toList();
    }
}
