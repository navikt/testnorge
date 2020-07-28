package no.nav.registre.populasjoner.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import no.nav.registre.populasjoner.repository.IdentModel;
import no.nav.registre.populasjoner.repository.IdentRepository;

@Controller
@RequiredArgsConstructor
public class TenorIdenterAdapter implements IdenterAdapter {

    private final IdentRepository identRepository;

    @Override
    public Set<String> getIdenter() {
        var identer = new HashSet<String>();
        identRepository.findAll().forEach(model -> identer.add(model.getFnr()));
        return identer;
    }

    public void saveIdent(String fnr) {
        var currentTime = LocalDateTime.now();
        var existingIdent = findIdentByFnr(fnr);
        if (existingIdent != null) {
            updateIdent(fnr);
        } else {
            identRepository.save(IdentModel.builder()
                    .fnr(fnr)
                    .datoOpprettet(currentTime)
                    .datoEndret(currentTime)
                    .build());
        }
    }

    private IdentModel findIdentByFnr(String fnr) {
        return identRepository.findByFnr(fnr).orElse(null);
    }

    private void updateIdent(String fnr) {
        updateIdent(findIdentByFnr(fnr));
    }

    private void updateIdent(IdentModel ident) {
        if (ident != null) {
            ident.setDatoEndret(LocalDateTime.now());
            identRepository.save(ident);
        }
    }
}
