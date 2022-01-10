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
import no.nav.organisasjonforvalter.util.UtenlandskAdresseUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestillingService {

    private static final String NORGE = "NO";

    private final AdresseServiceConsumer adresseServiceConsumer;
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;
    private final OrganisasjonOrgnummerServiceConsumer organisasjonOrgnummerServiceConsumer;
    private final OrganisasjonRepository organisasjonRepository;
    private final MapperFacade mapperFacade;

    private static String adresseQuery(AdresseRequest adresse) {

        var query = new StringBuilder();
        if (isNotBlank(adresse.getPostnr())) {
            query.append("postnummer=").append(adresse.getPostnr());
        }
        if (query.length() > 0) {
            query.append('&');
        }
        if (isNotBlank(adresse.getKommunenr())) {
            query.append("kommunenummer=").append(adresse.getKommunenr());
        }
        if (query.length() > 0) {
            query.append('&');
        }
        if (isNotBlank(adresse.getAdresselinjer().stream()
                .filter(linje -> isNotBlank(linje)).findFirst().orElse(""))) {
            query.append("fritekst=").append(adresse.getAdresselinjer().stream()
                    .filter(linje -> isNotBlank(linje)).findFirst().get());
        }
        return query.toString();
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

        getAdresse(orgRequest);

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

    private void getAdresse(OrganisasjonRequest orgRequest) {

        if (orgRequest.getAdresser().isEmpty()) {
            orgRequest.getAdresser().add(AdresseRequest.builder()
                    .adressetype(AdresseType.FADR)
                    .build());
        }

        orgRequest.getAdresser().forEach(adresse -> {

            if (isBlank(adresse.getLandkode()) || NORGE.equals(adresse.getLandkode())) {
                String query = adresseQuery(adresse);
                adresse.setAdresselinjer(new ArrayList<>());
                mapperFacade.map(adresseServiceConsumer.getAdresser(query).stream().findFirst().get(), adresse);

            } else {
                UtenlandskAdresseUtil.prepareUtenlandskAdresse(adresse);
            }
        });
    }
}
