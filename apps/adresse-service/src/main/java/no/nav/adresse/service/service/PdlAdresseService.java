package no.nav.adresse.service.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.adresse.service.consumer.PdlAdresseConsumer;
import no.nav.adresse.service.dto.AdresseRequest;
import no.nav.adresse.service.dto.GraphQLRequest;
import no.nav.adresse.service.dto.PdlAdresseResponse.Data;
import no.nav.adresse.service.dto.PdlAdresseResponse.Hits;
import no.nav.adresse.service.dto.PdlSearchRule;
import no.nav.registre.testnorge.libs.dto.adresseservice.v1.VegadresseDTO;
import org.apache.http.Consts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.adresse.service.dto.PdlSearchRule.CONTAINS;
import static no.nav.adresse.service.dto.PdlSearchRule.EQUALS;
import static no.nav.adresse.service.dto.PdlSearchRule.FUZZY;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
@Service
public class PdlAdresseService {

    private static final Long INITIAL_PAGESIZE = 100L;
    private final PdlAdresseConsumer pdlAdresseConsumer;
    private final MapperFacade mapperFacade;

    private final List<String> kommunenr;
    private final String pdlAdresseQuery;
    private final Map<String, Long> hitsCache = new HashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public PdlAdresseService(PdlAdresseConsumer pdlAdresseConsumer, MapperFacade mapperFacade) {

        this.pdlAdresseConsumer = pdlAdresseConsumer;
        this.mapperFacade = mapperFacade;
        pdlAdresseQuery = getQueryFromFile();
        kommunenr = getKommunerFromFile().keySet().stream().map(String.class::cast).collect(Collectors.toList());
    }

    private static String getQueryFromFile() {

        var resource = new ClassPathResource("pdladresse/pdlquery.graphql");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), Consts.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs feilet", e);
            return null;
        }
    }

    private static Properties getKommunerFromFile() {

        var resource = new ClassPathResource("kommuner/kommuner.yml");
        var kommuner = new Properties();

        try (final InputStream stream = resource.getInputStream()) {
            kommuner.load(stream);
        } catch (IOException e) {
            log.error("Lesing av kommuner feilet", e);
        }

        return kommuner;
    }

    private static GraphQLRequest.Criteria buildCriteria(String name, String value, PdlSearchRule rule) {

        return isNotBlank(value) ?
                GraphQLRequest.Criteria.builder()
                        .fieldName(name)
                        .searchRule(Map.of(rule.getName(), value))
                        .build() : null;
    }

    private static String serialize(AdresseRequest request) {

        return String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s",
                request.getMatrikkelId(),
                request.getAdressenavn(),
                request.getHusnummer(),
                request.getHusbokstav(),
                request.getPostnummer(),
                request.getKommunenummer(),
                request.getBydelsnummer(),
                request.getPoststed(),
                request.getKommunenavn(),
                request.getBydelsnavn(),
                request.getTilleggsnavn(),
                request.getFritekst());
    }

    public List<VegadresseDTO> getVegadresse(AdresseRequest request, Long antall) {

        if (isNull(request) || request.isEmpty()) {
            request = AdresseRequest.builder()
                    .kommunenummer(kommunenr.get((int) Math.floor(secureRandom.nextFloat() * kommunenr.size())))
                    .build();
        }

        var pageNumber = 0L;
        var resultsPerPage = INITIAL_PAGESIZE;

        var hits = hitsCache.getOrDefault(serialize(request), null);
        if (nonNull(hits)) {
            pageNumber = (long) (Math.floor(secureRandom.nextFloat() * hits / antall) % ((double) hits / antall - antall));
            resultsPerPage = antall;
        }

        var response = pdlAdresseConsumer.sendPdlAdresseSoek(GraphQLRequest.builder()
                .query(pdlAdresseQuery)
                .variables(Map.of(
                        "paging", GraphQLRequest.Paging.builder()
                                .pageNumber(pageNumber)
                                .resultsPerPage(resultsPerPage)
                                .build(),
                        "criteria",
                        Stream.of(
                                buildCriteria("vegadresse.adressenavn", request.getAdressenavn(), EQUALS),
                                buildCriteria("vegadresse.husnummer", request.getHusnummer(), EQUALS),
                                buildCriteria("vegadresse.husbokstav", request.getHusbokstav(), EQUALS),
                                buildCriteria("vegadresse.postnummer", request.getPostnummer(), EQUALS),
                                buildCriteria("vegadresse.poststed", request.getPoststed(), EQUALS),
                                buildCriteria("vegadresse.kommunenummer", request.getKommunenummer(), EQUALS),
                                buildCriteria("vegadresse.kommunenavn", request.getKommunenavn(), EQUALS),
                                buildCriteria("vegadresse.bydelsnummer", request.getBydelsnummer(), EQUALS),
                                buildCriteria("vegadresse.bydelsnavn", request.getBydelsnavn(), EQUALS),
                                buildCriteria("vegadresse.tilleggsnavn", request.getTilleggsnavn(), FUZZY),
                                buildCriteria("vegadresse.matrikkelId", request.getMatrikkelId(), EQUALS),
                                buildCriteria("vegadresse.fritekst", request.getFritekst(), CONTAINS)
                        )
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())))
                .build());
        if (nonNull(response.getData().getSokAdresse())) {
            hitsCache.put(serialize(request), response.getData().getSokAdresse().getTotalHits());
        }
        return mapperFacade.mapAsList(getSublist(response.getData(), antall, request), VegadresseDTO.class);
    }

    private List<Hits> getSublist(Data data, long antall, AdresseRequest request) {

        if (isNull(data.getSokAdresse()) || data.getSokAdresse().getHits().isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Ingen adresse funnet, request: " + request.toString());

        } else if (data.getSokAdresse().getHits().size() == antall) {
            return data.getSokAdresse().getHits();

        } else {
            var length = Math.min(antall, data.getSokAdresse().getHits().size());
            int startIndex = (int) Math.floor(secureRandom.nextFloat() * (data.getSokAdresse().getHits().size() - length));
            return data.getSokAdresse().getHits().subList(startIndex, startIndex + (int) length);
        }
    }
}
