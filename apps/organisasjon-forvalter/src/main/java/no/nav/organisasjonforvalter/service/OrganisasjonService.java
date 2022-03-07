package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import no.nav.organisasjonforvalter.dto.responses.UnderenhetResponse;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganisasjonService {

    private final MapperFacade mapperFacade;
    private final OrganisasjonRepository organisasjonRepository;

    public List<RsOrganisasjon> getOrganisasjoner(List<String> orgnumre) {

        return mapperFacade.mapAsList(organisasjonRepository.findAllByOrganisasjonsnummerIn(orgnumre), RsOrganisasjon.class);
    }

    public List<RsOrganisasjon> getOrganisasjoner(String brukerid) {
        return mapperFacade.mapAsList(organisasjonRepository.findOrganisasjonerByBrukerId(brukerid), RsOrganisasjon.class);
    }
}
