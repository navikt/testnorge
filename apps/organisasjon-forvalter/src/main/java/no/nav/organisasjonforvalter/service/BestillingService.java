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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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

    public Mono<BestillingResponse> execute(BestillingRequest request) {

        return Flux.fromIterable(request.getOrganisasjoner())
                .flatMap(org -> processOrganisasjon(org, null))
                .map(Organisasjon::getOrganisasjonsnummer)
                .collect(Collectors.toSet())
                .map(orgnumre -> BestillingResponse.builder().orgnummer(orgnumre).build());
    }

    private Mono<Organisasjon> processOrganisasjon(OrganisasjonRequest orgRequest, Organisasjon parent) {

        return setAdresse(orgRequest.getAdresser())
                .then(Mono.defer(() -> {
                    Organisasjon organisasjon = mapperFacade.map(orgRequest, Organisasjon.class);
                    organisasjon.setUnderenheter(mapperFacade.mapAsList(organisasjon.getUnderenheter(), Organisasjon.class));
                    organisasjon.setParent(parent);

                    return Mono.zip(
                                    organisasjonOrgnummerServiceConsumer.getOrgnummer(),
                                    genererNavnServiceConsumer.getOrgName(),
                                    CurrentAuthentication.getUserId()
                            )
                            .flatMap(tuple -> {
                                organisasjon.setOrganisasjonsnummer(tuple.getT1());
                                organisasjon.setOrganisasjonsnavn(tuple.getT2());
                                organisasjon.setBrukerId(tuple.getT3());

                                if (orgRequest.getUnderenheter().isEmpty()) {
                                    return Mono.fromCallable(() -> organisasjonRepository.save(organisasjon))
                                            .subscribeOn(Schedulers.boundedElastic());
                                } else {
                                    return Flux.fromIterable(orgRequest.getUnderenheter())
                                            .flatMap(underenhet -> processOrganisasjon(underenhet, organisasjon))
                                            .then(Mono.just(organisasjon));
                                }
                            });
                }));
    }

    private Mono<Void> setAdresse(List<AdresseRequest> adresseRequest) {

        if (adresseRequest.isEmpty()) {
            adresseRequest.add(AdresseRequest.builder()
                    .adressetype(AdresseType.FADR)
                    .build());
        }

        return Flux.fromIterable(adresseRequest)
                .flatMap(adresse -> {
                    if (isBlank(adresse.getLandkode()) || NORGE.equals(adresse.getLandkode())) {
                        val query = adresseQuery(adresse);
                        return adresseServiceConsumer.getAdresser(query)
                                .doOnNext(adresser -> adresser.stream().findFirst()
                                        .ifPresent(adressedetaljer ->
                                                mapperFacade.map(adressedetaljer, adresse)));
                    } else {
                        UtenlandskAdresseUtil.prepareUtenlandskAdresse(adresse);
                        return Mono.empty();
                    }
                })
                .then();
    }

    private static String adresseQuery(AdresseRequest adresse) {

        val query = new StringBuilder();
        if (isNotBlank(adresse.getPostnr())) {
            query.append("postnummer=").append(adresse.getPostnr());
        }

        if (isNotBlank(adresse.getKommunenr())) {
            if (!query.isEmpty()) {
                query.append('&');
            }
            query.append("kommunenummer=").append(adresse.getKommunenr());
        }

        val adressefragment = adresse.getAdresselinjer().stream()
                .filter(StringUtils::isNotBlank)
                .map(adresselinje -> adresselinje.split("\\s+",2)[0])
                .findFirst()
                .orElse("");

        if (isNotBlank(adressefragment)) {
            if (!query.isEmpty()) {
                query.append('&');
            }
            query.append("fritekst=")
                    .append(adressefragment);
        }
        return query.toString();
    }
}
