package no.nav.registre.testnorge.arbeidsforhold.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforhold.domain.OppsummeringsdokumentetRawList;
import no.nav.registre.testnorge.arbeidsforhold.repository.OppsummeringsdokumentetRepository;
import no.nav.registre.testnorge.arbeidsforhold.repository.model.OppsummeringsdokumentetModel;

@Slf4j
@Component
@RequiredArgsConstructor
public class OppsummeringsdokumentetRawAdapter {
    private static final int PAGE_SIZE = 1;
    private final OppsummeringsdokumentetRepository opplysningspliktigRepository;

    public OppsummeringsdokumentetRawList fetchAll(String mijlo, int page) {
        return new OppsummeringsdokumentetRawList(
                opplysningspliktigRepository.findAllByLast(mijlo, PageRequest.of(page, PAGE_SIZE))
        );
    }
}
