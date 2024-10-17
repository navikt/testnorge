package no.nav.testnav.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import no.nav.testnav.levendearbeidsforholdansettelse.repository.AnsettelseLoggRepository;
import no.nav.testnav.levendearbeidsforholdansettelse.repository.LoggRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LoggService {

    private final LoggRepository loggRepository;
    private final AnsettelseLoggRepository ansettelseLoggRepository;

    public Flux<Page<AnsettelseLogg>> getAnsettelseLogg(Pageable pageable) {

        return loggRepository.findAllBy(pageable);
    }

    public Flux<AnsettelseLogg> getIdent(String ident) {

        return ansettelseLoggRepository.findByFolkeregisterident(ident);
    }

    public Flux<AnsettelseLogg> getOrgnummer(String orgnummer) {

        return ansettelseLoggRepository.findByOrganisasjonsnummer(orgnummer);
    }
}
