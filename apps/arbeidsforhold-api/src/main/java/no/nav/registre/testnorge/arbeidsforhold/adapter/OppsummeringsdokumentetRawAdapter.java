package no.nav.registre.testnorge.arbeidsforhold.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import no.nav.registre.testnorge.arbeidsforhold.domain.OppsummeringsdokumentetRawList;
import no.nav.registre.testnorge.arbeidsforhold.repository.OppsummeringsdokumentetRepository;
import no.nav.registre.testnorge.arbeidsforhold.repository.model.OppsummeringsdokumentetModel;

@Slf4j
@Component
@RequiredArgsConstructor
public class OppsummeringsdokumentetRawAdapter {
    private final OppsummeringsdokumentetRepository opplysningspliktigRepository;

    public String findById(Long id){
        return opplysningspliktigRepository.findById(id).map(OppsummeringsdokumentetModel::getDocument).orElse(null);
    }

    public OppsummeringsdokumentetRawList fetchBy(String miljo, int page, LocalDate fom, LocalDate tom) {
        if (fom != null && tom != null) {
            return new OppsummeringsdokumentetRawList(
                    opplysningspliktigRepository.findAllByLastWithFomAndTom(miljo, fom.getYear(), fom.getMonthValue(), tom.getYear(), tom.getMonthValue(), PageRequest.of(page, 1))
            );
        }

        if (fom != null) {
            return new OppsummeringsdokumentetRawList(
                    opplysningspliktigRepository.findAllByLastWithFom(miljo, fom.getYear(), fom.getMonthValue(), PageRequest.of(page, 1))
            );
        }

        if (tom != null) {
            return new OppsummeringsdokumentetRawList(
                    opplysningspliktigRepository.findAllByLastWithTom(miljo, tom.getYear(), tom.getMonthValue(), PageRequest.of(page, 1))
            );
        }

        return new OppsummeringsdokumentetRawList(
                opplysningspliktigRepository.findAllByLast(miljo, PageRequest.of(page, 1))
        );
    }

}
