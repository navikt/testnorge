package no.nav.registre.populasjoner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.populasjoner.domain.Ident;
import no.nav.registre.populasjoner.repository.IdentRepository;

@Service
@RequiredArgsConstructor
public class IdentService {

    private final IdentRepository identRepository;

    public Ident findIdentByFnr(String fnr) {
        return identRepository.findByFnr(fnr).orElse(null);
    }

    public List<Ident> findAllIdents() {
        var identer = new ArrayList<Ident>();
        identRepository.findAll().forEach(identer::add);
        return identer;
    }

    public Ident saveIdentWithFnr(String fnr) {
        var currentTime = LocalDateTime.now();
        var existingIdent = findIdentByFnr(fnr);
        if (existingIdent != null) {
            return updateIdentWithFnr(fnr);
        } else {
            return identRepository.save(Ident.builder()
                    .fnr(fnr)
                    .datoOpprettet(currentTime)
                    .datoEndret(currentTime)
                    .build());
        }
    }

    public Ident updateIdentWithFnr(String fnr) {
        return updateIdent(identRepository.findByFnr(fnr).orElse(null));
    }

    public Ident deleteIdentWithFnr(String fnr) {
        return deleteIdent(identRepository.findByFnr(fnr).orElse(null));
    }

    private Ident updateIdent(Ident ident) {
        if (ident != null) {
            ident.setDatoEndret(LocalDateTime.now());
            identRepository.save(ident);
        }
        return ident;
    }

    private Ident deleteIdent(Ident ident) {
        if (ident != null) {
            identRepository.delete(ident);
        }
        return ident;
    }
}
