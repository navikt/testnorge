package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

    public BestillingResponse execute(BestillingRequest request) {

        val orgnumre = request.getOrganisasjoner().stream()
                .map(org -> {
                    Organisasjon parent = processOrganisasjon(org, null);
                    return parent.getOrganisasjonsnummer();
                })
                .collect(Collectors.toSet());

        return BestillingResponse.builder().orgnummer(orgnumre).build();
    }

    private Organisasjon processOrganisasjon(OrganisasjonRequest orgRequest, Organisasjon parent) {

        setAdresse(orgRequest.getAdresser());

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

    private void setAdresse(List<AdresseRequest> adresseRequest) {

        if (adresseRequest.isEmpty()) {
            adresseRequest.add(AdresseRequest.builder()
                    .adressetype(AdresseType.FADR)
                    .build());
        }

        adresseRequest.forEach(adresse -> {

            if (isBlank(adresse.getLandkode()) || NORGE.equals(adresse.getLandkode())) {

                val query = adresseQuery(adresse);
                adresseServiceConsumer.getAdresser(query).stream().findFirst()
                        .ifPresent(adressedetaljer ->
                                mapperFacade.map(adressedetaljer, adresse));
            } else {
                UtenlandskAdresseUtil.prepareUtenlandskAdresse(adresse);
            }
        });
    }

    private static String adresseQuery(AdresseRequest adresse) {

        val query = new StringBuilder();
        if (isNotBlank(adresse.getPostnr())) {
            query.append("postnummer=").append(adresse.getPostnr());
        }
        if (!query.isEmpty()) {
            query.append('&');
        }
        if (isNotBlank(adresse.getKommunenr())) {
            query.append("kommunenummer=").append(adresse.getKommunenr());
        }
        if (!query.isEmpty()) {
            query.append('&');
        }
        val adressefragment = adresse.getAdresselinjer().stream()
                .filter(StringUtils::isNotBlank)
                .map(adresselinje -> adresselinje.split(" ")[0])
                .findFirst()
                .orElse("");

        if (isNotBlank(adressefragment)) {
            query.append("fritekst=")
                    .append(adressefragment);
        }
        return query.toString();
    }
}
