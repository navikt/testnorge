package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.AnsettelseLoggRepository;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.LoggRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoggService {

    private final LoggRepository loggRepository;
    private final AnsettelseLoggRepository ansettelseLoggRepository;

    @Transactional(readOnly = true)
    public Page<AnsettelseLogg> getAnsettelseLogg(Pageable pageable) {

        return loggRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<AnsettelseLogg> getIdent(String ident) {

        return ansettelseLoggRepository.findByFolkeregisterident(ident);
    }

    @Transactional(readOnly = true)
    public List<AnsettelseLogg> getOrgnummer(String orgnummer) {

        return ansettelseLoggRepository.findByOrganisasjonsnummer(orgnummer);
    }
}
