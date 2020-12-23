package no.nav.registre.testnorge.arbeidsforhold.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import no.nav.registre.testnorge.arbeidsforhold.domain.OppsummeringsdokumentetRawList;
import no.nav.registre.testnorge.arbeidsforhold.repository.OppsummeringsdokumentetRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class OppsummeringsdokumentetRawAdapter {
    private final OppsummeringsdokumentetRepository opplysningspliktigRepository;

    public OppsummeringsdokumentetRawList fetchBy(String mijlo, int position, LocalDate fom, LocalDate tom) {
        if (fom != null && tom != null) {
            return new OppsummeringsdokumentetRawList(
                    opplysningspliktigRepository.findAllByLastWithFomAndTom(mijlo, fom.getYear(), fom.getMonthValue(), tom.getYear(), tom.getMonthValue(), PageRequest.of(position, 1))
            );
        }

        if (fom != null) {
            return new OppsummeringsdokumentetRawList(
                    opplysningspliktigRepository.findAllByLastWithFom(mijlo, fom.getYear(), fom.getMonthValue(), PageRequest.of(position, 1))
            );
        }

        if (tom != null) {
            return new OppsummeringsdokumentetRawList(
                    opplysningspliktigRepository.findAllByLastWithTom(mijlo, tom.getYear(), tom.getMonthValue(), PageRequest.of(position, 1))
            );
        }

        return new OppsummeringsdokumentetRawList(
                opplysningspliktigRepository.findAllByLast(mijlo, PageRequest.of(position, 1))
        );
    }

}
