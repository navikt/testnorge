package no.nav.registre.populasjoner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import no.nav.registre.populasjoner.domain.Ident;
import no.nav.registre.populasjoner.repository.IdentRepository;

@Service
@RequiredArgsConstructor
public class IdentService {

    private final IdentRepository identRepository;

    public Ident findIdentById(Long id) {
        return identRepository.findById(id).orElse(null);
    }

    public Ident saveIdentWithFnr(String fnr) {
        var currentTime = LocalDateTime.now();
        return identRepository.save(Ident.builder()
                .fnr(fnr)
                .datoOpprettet(currentTime)
                .datoEndret(currentTime)
                .build());
    }

    public Ident updateIdentWithId(Long id) {
        var ident = identRepository.findById(id).orElse(null);
        if (ident != null) {
            ident.setDatoEndret(LocalDateTime.now());
            identRepository.save(ident);
        }
        return ident;
    }

    public Ident deleteIdentWithId(Long id) {
        var ident = identRepository.findById(id).orElse(null);
        if (ident != null) {
            identRepository.delete(ident);
        }
        return ident;
    }
}
