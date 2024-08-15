package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.LoggRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoggService {

    private final LoggRepository loggRepository;
    public Page<AnsettelseLogg> getAnsettelseLogg(Pageable pageable)  {

        return loggRepository.findAll(pageable);
    }
}
