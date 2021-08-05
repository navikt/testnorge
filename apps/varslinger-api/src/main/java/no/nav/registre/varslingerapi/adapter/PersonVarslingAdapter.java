package no.nav.registre.varslingerapi.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.testnav.libs.avro.personinfo.PersonInfo;
import no.nav.testnav.libs.servletsecurity.service.AuthenticationTokenResolver;
import no.nav.registre.varslingerapi.domain.Varsling;
import no.nav.registre.varslingerapi.repository.BrukerRepository;
import no.nav.registre.varslingerapi.repository.MottattVarslingRepository;
import no.nav.registre.varslingerapi.repository.model.BrukerModel;
import no.nav.registre.varslingerapi.repository.model.MottattVarslingModel;

@Component
@RequiredArgsConstructor
public class PersonVarslingAdapter {

    private final MottattVarslingRepository mottattVarslingRepository;
    private final BrukerRepository brukerRepository;
    private final AuthenticationTokenResolver authenticationTokenResolver;
    private final VarslingerAdapter varslingerAdapter;

    private BrukerModel getBruker() {
        if (authenticationTokenResolver.isClientCredentials()) {
            throw new BadCredentialsException("Kan ikke hente ut bruker fra en ikke-personlig innlogging.");
        }
        var oid = authenticationTokenResolver.getOid();
        return brukerRepository
                .findById(oid)
                .orElseGet(() -> brukerRepository.save(
                        BrukerModel
                                .builder()
                                .objectId(oid)
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
        Varsling varsling = varslingerAdapter.get(varslingId);

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

    public void deleteAll(PersonInfo personInfo) {
        var objectId = personInfo.getObjectId().toString();
        mottattVarslingRepository.deleteAllByBrukerObjectId(objectId);
        brukerRepository.deleteById(objectId);
    }
}