package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.consumer.OrganisasjonNavnConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonNummerConsumer;
import no.nav.organisasjonforvalter.consumer.TpsfAdresseConsumer;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest.AdresseRequest;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest.AdresseType;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest.OrganisasjonRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.BestillingResponse;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BestillingService {

    private final OrganisasjonNavnConsumer organisasjonNavnConsumer;
    private final OrganisasjonNummerConsumer organisasjonNummerConsumer;
    private final OrganisasjonRepository organisasjonRepository;
    private final TpsfAdresseConsumer tpsfAdresseConsumer;
    private final MapperFacade mapperFacade;

    public BestillingResponse execute(BestillingRequest request) {

        Set<String> orgnumre = request.getOrganisasjoner().stream()
                .map(org -> {
                    Organisasjon parent = processOrganisasjon(org, null);
                    return parent.getOrganisasjonsnummer();
                })
                .collect(Collectors.toSet());

        return BestillingResponse.builder().orgnummer(orgnumre).build();
    }

    private Organisasjon processOrganisasjon(OrganisasjonRequest orgRequest, Organisasjon parent) {

        fixAdresseFallback(orgRequest);

        Organisasjon organisasjon = mapperFacade.map(orgRequest, Organisasjon.class);
        organisasjon.setOrganisasjonsnummer(organisasjonNummerConsumer.getOrgnummer());
        organisasjon.setOrganisasjonsnavn(organisasjonNavnConsumer.getOrgName());
        organisasjon.setParent(parent);
        if (orgRequest.getUnderenheter().isEmpty()) {
            organisasjonRepository.save(organisasjon);
        } else {
            orgRequest.getUnderenheter().forEach(underenhet -> processOrganisasjon(underenhet, organisasjon));
        }
        return organisasjon;
    }

    private void fixAdresseFallback(OrganisasjonRequest orgRequest) {

        if (orgRequest.getAdresser().isEmpty()) {
            orgRequest.getAdresser().add(AdresseRequest.builder()
                    .adressetype(AdresseType.FADR)
                    .build());
        }

        orgRequest.getAdresser().forEach(adresse -> {
            if (adresse.getAdresselinjer().isEmpty()) {
                mapperFacade.map(tpsfAdresseConsumer.getAdresser(adresse.getPostnr(), adresse.getKommunenr()), adresse);
            }
        });
    }
}
