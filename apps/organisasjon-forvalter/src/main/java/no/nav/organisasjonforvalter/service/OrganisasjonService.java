package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganisasjonService {

    private final OrganisasjonRepository organisasjonRepository;

    public List<Organisasjon> getOrganisasjoner(List<String> orgnumre){

        return organisasjonRepository.findAllByOrganisasjonsnummerIn(orgnumre);
    }
}
