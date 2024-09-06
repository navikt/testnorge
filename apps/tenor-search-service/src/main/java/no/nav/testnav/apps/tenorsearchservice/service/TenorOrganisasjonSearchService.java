package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.TenorOrganisasjonConsumer;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.InfoType;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOrganisasjonRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktOrganisasjonResponse;
import no.nav.testnav.apps.tenorsearchservice.mapper.TenorOrganisasjonResultMapperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.util.Objects.nonNull;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertBooleanWildcard;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertIntervall;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertObject;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.guard;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenorOrganisasjonSearchService {

    private final TenorOrganisasjonConsumer tenorClient;
    private final TenorOrganisasjonResultMapperService tenorOrganisasjonResultMapperService;

    public Mono<TenorOversiktOrganisasjonResponse> getTestdataOversiktOrganisasjon(TenorOrganisasjonRequest searchData, Integer antall, Integer side, Integer seed) {

        var query = getOrganisasjonQuery(searchData);

        return tenorClient.getOrganisasjonTestdata(query, InfoType.Organisasjon, antall, side, seed)
                .flatMap(resultat -> Mono.just(tenorOrganisasjonResultMapperService.mapOrganisasjon(resultat, query)));
    }

    public Mono<TenorOversiktOrganisasjonResponse> getTestdataOrganisasjon(TenorOrganisasjonRequest searchData) {

        var query = getOrganisasjonQuery(searchData);

        return tenorClient.getOrganisasjonTestdata(query, InfoType.AlleFelter, null, null, null)
                .flatMap(resultat -> Mono.just(tenorOrganisasjonResultMapperService.mapOrganisasjon(resultat, query)));
    }

    private String getOrganisasjonQuery(TenorOrganisasjonRequest searchData) {

        var builder = new StringBuilder()
                .append(convertObject("organisasjonsnummer", searchData.getOrganisasjonsnummer()))
                .append(convertObject("harUtenlandskForretningsadresse", searchData.getHarUtenlandskForretningsadresse()))
                .append(convertObject("harUtenlandskPostadresse", searchData.getHarUtenlandskPostadresse()))
                .append(convertObject("naeringBeskrivelse", searchData.getNaeringBeskrivelse()))
                .append(convertObject("naeringKode", searchData.getNaeringKode()))
                .append(convertObject("registrertIMvaregisteret", searchData.getRegistrertIMvaregisteret()))
                .append(convertObject("registrertIForetaksregisteret", searchData.getRegistrertIForetaksregisteret()))
                .append(convertObject("registrertIFrivillighetsregisteret", searchData.getRegistrertIFrivillighetsregisteret()))
                .append(convertObject("slettetIEnhetsregisteret", searchData.getSlettetIEnhetsregisteret()))
                .append(convertIntervall("antallAnsatte", searchData.getAntallAnsatte()))

                .append(convertBooleanWildcard("revisorer", searchData.getRevisorer()))
                .append(convertBooleanWildcard("regnskapsfoerere", searchData.getRegnskapsfoerere()))
                .append(convertBooleanWildcard("dagligLeder", searchData.getDagligLeder()))
                .append(convertBooleanWildcard("styremedlemmer", searchData.getStyremedlemmer()))
                .append(convertBooleanWildcard("forretningsfoerer", searchData.getForretningsfoerer()))
                .append(convertBooleanWildcard("kontaktpersoner", searchData.getKontaktpersoner()))
                .append(convertBooleanWildcard("norsk_representant", searchData.getNorsk_representant()))

                .append(convertBooleanWildcard("harUnderenheter", searchData.getHarUnderenheter()))
                .append(convertObject("antallUnderenheter", searchData.getAntallUnderenheter()));


        if (nonNull(searchData.getOrganisasjonsform())) {
            builder.append(convertObject("organisasjonsform.kode", searchData.getOrganisasjonsform().getKode()));
        }

        if (nonNull(searchData.getForretningsadresse())) {
            builder.append(convertObject("forretningsadresse.kommunenummer", searchData.getForretningsadresse().getKommunenummer()));
        }

        if (nonNull(searchData.getEnhetStatuser())) {
            builder.append(convertObject("enhetStatuser.kode", searchData.getEnhetStatuser().getKode()));
        }

        if (nonNull(searchData.getErUnderenhet())) {
            builder.append(convertBooleanWildcard("erUnderenhet.hovedenhet", searchData.getErUnderenhet().getHovedenhet()));
        }

        builder.append(TenorOrganisasjonEksterneRelasjonerUtility.getOrganisasjonEksterneRelasjoner(searchData));

        return guard(builder);
    }
}
