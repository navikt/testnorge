package no.nav.registre.testnorge.arbeidsforhold.domain;

import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforhold.repository.model.OppsummeringsdokumentetModel;

@Value
public class OppsummeringsdokumentetRawList {
    List<String> documents;
    Integer numberOfPages;

    public OppsummeringsdokumentetRawList(Page<OppsummeringsdokumentetModel> pages) {
        this.documents = pages.stream().map(OppsummeringsdokumentetModel::getDocument).collect(Collectors.toList());
        this.numberOfPages = pages.getTotalPages();
    }
}
