package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.consumer.AdresseServiceConsumer;
import no.nav.organisasjonforvalter.consumer.GenererNavnServiceConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonOrgnummerServiceConsumer;
import no.nav.organisasjonforvalter.dto.requests.BestillingRequest;
import no.nav.organisasjonforvalter.dto.requests.BestillingRequest.AdresseRequest;
import no.nav.organisasjonforvalter.dto.requests.BestillingRequest.AdresseType;
import no.nav.organisasjonforvalter.dto.requests.BestillingRequest.OrganisasjonRequest;
import no.nav.organisasjonforvalter.dto.responses.BestillingResponse;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import no.nav.organisasjonforvalter.util.CurrentAuthentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestillingService {

    private final AdresseServiceConsumer adresseServiceConsumer;
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;
    private final OrganisasjonOrgnummerServiceConsumer organisasjonOrgnummerServiceConsumer;
    private final OrganisasjonRepository organisasjonRepository;
    private final MapperFacade mapperFacade;

    private static String queryBuilder(AdresseRequest adresse) {

        return new StringBuilder("postnummer=")
                .append(isNotBlank(adresse.getPostnr()) ? adresse.getPostnr() : "")
                .append("&kommunenummer=")
                .append(isNotBlank(adresse.getKommunenr()) ? adresse.getKommunenr() : "")
                .append("&fritekst=")
                .append(adresse.getAdresselinjer().stream().findFirst().orElse(""))
                .toString();
    }

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
        organisasjon.setOrganisasjonsnummer(organisasjonOrgnummerServiceConsumer.getOrgnummer());
        organisasjon.setOrganisasjonsnavn(genererNavnServiceConsumer.getOrgName());
        organisasjon.setUnderenheter(mapperFacade.mapAsList(organisasjon.getUnderenheter(), Organisasjon.class));
        organisasjon.setParent(parent);
        organisasjon.setBrukerId(CurrentAuthentication.getUserId());

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
            String query = queryBuilder(adresse);
            adresse.setAdresselinjer(new ArrayList<>());
            mapperFacade.map(adresseServiceConsumer.getAdresser(query).stream().findFirst().get(), adresse);
        });
    }
}
