package no.nav.registre.testnorge.hendelse.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.hendelse.domain.Hendelse;
import no.nav.registre.testnorge.hendelse.repository.HendelseRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class HendelseAdapter {
    private static final String HENDELSE_KEY = "Testnorge-Hendelse";
    private final HendelseRepository hendelseRepository;

    public void opprett(Hendelse hendelse) {
        MDC.put(HENDELSE_KEY, hendelse.getType().name());
        log.trace(hendelse.toString());
        hendelseRepository.save(hendelse.toHendelseModel());
    }

    public List<Hendelse> hentHendelser(String ident) {
        return hendelseRepository
                .findByIdent(ident)
                .stream()
                .map(Hendelse::new)
                .collect(Collectors.toList());
    }

    public List<Hendelse> hentHendelser() {
        return hendelseRepository
                .findAll()
                .stream()
                .map(Hendelse::new)
                .collect(Collectors.toList());
    }
}
