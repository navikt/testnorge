package no.nav.registre.testnorge.hendelse.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.hendelse.domain.Hendelse;
import no.nav.registre.testnorge.hendelse.repository.HendelseRepository;
import no.nav.registre.testnorge.hendelse.repository.IdentRepository;
import no.nav.registre.testnorge.hendelse.repository.model.IdentModel;

@Component
@RequiredArgsConstructor
public class HendelseAdapter {
    private final HendelseRepository hendelseRepository;
    private final IdentRepository identRepository;


    private IdentModel hentIdent(String ident) {
        return identRepository
                .findByIdent(ident)
                .orElseGet(() -> identRepository.save(new IdentModel(ident)));
    }

    public void opprett(Hendelse hendelse) {
        var ident = hentIdent(hendelse.getIdent());
        hendelseRepository.save(hendelse.toHendelseModel(ident));
    }


    public List<Hendelse> hentHendelser(String ident) {
        Optional<IdentModel> identModel = identRepository.findByIdent(ident);
        if (identModel.isEmpty()) {
            return Collections.emptyList();
        }
        return hendelseRepository
                .findByIdentId(identModel.get().getId())
                .stream()
                .map(Hendelse::new)
                .collect(Collectors.toList());
    }
}
