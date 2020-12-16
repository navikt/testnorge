package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.consumer.OrganisasjonNavnConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonNummerConsumer;
import no.nav.organisasjonforvalter.jpa.entity.Adresse;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.AdresseRepository;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.BestillingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BestillingService {

    private final OrganisasjonNavnConsumer organisasjonNavnConsumer;
    private final OrganisasjonNummerConsumer organisasjonNummerConsumer;
    private final OrganisasjonRepository organisasjonRepository;
    private final AdresseRepository adresseRepository;
    private final MapperFacade mapperFacade;

    public BestillingResponse execute(BestillingRequest request) {

        List<String> orgNumre = request.getOrganisasjoner().stream()
                .map(org -> {
                    Organisasjon organisasjon = mapperFacade.map(org, Organisasjon.class);
                    organisasjon.setOrganisasjonsnummer(organisasjonNummerConsumer.getOrgnummer().substring(0, 9));
                    organisasjon.setOrganisasjonsnavn(organisasjonNavnConsumer.getOrgName());
                    Organisasjon dbOrganisasjon = organisasjonRepository.save(organisasjon);
                    List<Adresse> adresser = mapperFacade.mapAsList(org.getAdresser(), Adresse.class);
                    adresser.forEach(adr -> adr.setOrganisasjonId(dbOrganisasjon.getId()));
                    adresseRepository.saveAll(adresser);

                    return organisasjon.getOrganisasjonsnummer();
                })
                .collect(Collectors.toList());

        return BestillingResponse.builder().orgnummer(orgNumre).build();
    }
}
