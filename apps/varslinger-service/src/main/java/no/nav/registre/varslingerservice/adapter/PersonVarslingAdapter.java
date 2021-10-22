package no.nav.registre.varslingerservice.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.varslingerservice.repository.BrukerRepository;
import no.nav.registre.varslingerservice.repository.MottattVarslingRepository;
import no.nav.registre.varslingerservice.repository.model.BrukerModel;
import no.nav.registre.varslingerservice.repository.model.MottattVarslingModel;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedId;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedToken;

@Component
@RequiredArgsConstructor
public class PersonVarslingAdapter {

    private final MottattVarslingRepository mottattVarslingRepository;
    private final BrukerRepository brukerRepository;
    private final VarslingerAdapter varslingerAdapter;
    private final GetAuthenticatedId getAuthenticatedId;
    private final GetAuthenticatedToken getAuthenticatedToken;

    private BrukerModel getBruker() {
        if (getAuthenticatedToken.call().clientCredentials()) {
            throw new BadCredentialsException("Kan ikke hente ut bruker fra en ikke-personlig innlogging.");
        }
        var id = getAuthenticatedId.call();
        return brukerRepository
                .findById(id)
                .orElseGet(() -> brukerRepository.save(
                        BrukerModel
                                .builder()
                                .objectId(id)
                                .build()
                ));
    }

    private Optional<MottattVarslingModel> getMottattVarsling(String varslingId) {
        String objectId = getBruker().getObjectId();
        return mottattVarslingRepository
                .findAllByBrukerObjectId(objectId)
                .stream()
                .filter(mottattVarslingModel -> mottattVarslingModel.getVarsling().getVarslingId().equals(varslingId))
                .findFirst();
    }

    public List<String> getAll() {
        BrukerModel bruker = getBruker();
        var mottattVarslinger = mottattVarslingRepository.findAllByBrukerObjectId(bruker.getObjectId());
        return mottattVarslinger
                .stream()
                .map(value -> value.getVarsling().getVarslingId())
                .collect(Collectors.toList());
    }

    public String save(String varslingId) {
        var varsling = varslingerAdapter.get(varslingId);

        if (getMottattVarsling(varsling.getVarslingId()).isPresent()) {
            return varsling.getVarslingId();
        }

        var saved = mottattVarslingRepository.save(
                MottattVarslingModel
                        .builder()
                        .bruker(getBruker())
                        .varsling(varsling.toModel())
                        .build()
        );
        return saved.getVarsling().getVarslingId();
    }

    public void delete(String varslingId) {
        getMottattVarsling(varslingId).ifPresent(mottattVarslingRepository::delete);
    }

    public String get(String varslingId) {
        Optional<MottattVarslingModel> mottattVarsling = getMottattVarsling(varslingId);
        return mottattVarsling.map(mottatt -> mottatt.getVarsling().getVarslingId()).orElse(null);
    }
}