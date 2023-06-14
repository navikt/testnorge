package no.nav.registre.varslingerservice.adapter;

import lombok.RequiredArgsConstructor;
import no.nav.registre.varslingerservice.domain.Varsling;
import no.nav.registre.varslingerservice.repository.VarslingRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class VarslingerAdapter {
    private final VarslingRepository varslingRepository;

    public List<Varsling> getAll() {
        return StreamSupport
                .stream(varslingRepository.findAll().spliterator(), false)
                .map(Varsling::new)
                .toList();
    }

    public String save(Varsling varsling) {
        var varslingModel = varsling.toModel();
        return varslingRepository.save(varslingModel).getVarslingId();
    }

    public void delete(String varslingId) {
        varslingRepository.deleteById(varslingId);
    }

    public Varsling get(String varslingID) {
        return varslingRepository
                .findById(varslingID)
                .map(Varsling::new)
                .orElse(null);
    }
}
