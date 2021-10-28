package no.nav.testnav.apps.adresseservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.apps.adresseservice.consumer.PdlAdresseConsumer;
import no.nav.testnav.apps.adresseservice.dto.GraphQLRequest;
import no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseRequest;
import no.nav.testnav.apps.adresseservice.dto.PdlSearchRule;
import no.nav.testnav.apps.adresseservice.dto.VegadresseRequest;
import no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import org.apache.http.Consts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.apps.adresseservice.dto.PdlSearchRule.CONTAINS;
import static no.nav.testnav.apps.adresseservice.dto.PdlSearchRule.EQUALS;
import static no.nav.testnav.apps.adresseservice.dto.PdlSearchRule.EXISTS;
import static no.nav.testnav.apps.adresseservice.dto.PdlSearchRule.FUZZY;
import static no.nav.testnav.apps.adresseservice.dto.PdlSearchRule.RANDOM;

@Slf4j
@Service
public class PdlAdresseService {

    private final Random random = new SecureRandom();
    private final PdlAdresseConsumer pdlAdresseConsumer;
    private final MapperFacade mapperFacade;
    private final String vegAdresseQuery;
    private final String matrikkelAdresseQuery;

    public PdlAdresseService(PdlAdresseConsumer pdlAdresseConsumer, MapperFacade mapperFacade) {

        this.pdlAdresseConsumer = pdlAdresseConsumer;
        this.mapperFacade = mapperFacade;
        vegAdresseQuery = getQueryFromFile("pdladresse/vegadressequery.graphql");
        matrikkelAdresseQuery = getQueryFromFile("pdladresse/matrikkeladressequery.graphql");
    }

    private static String getQueryFromFile(String path) {

        var resource = new ClassPathResource(path);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), Consts.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs feilet", e);
            return null;
        }
    }

    private static GraphQLRequest.Criteria buildCriteria(String name, Object value, PdlSearchRule rule) {

        return nonNull(value) ?
                GraphQLRequest.Criteria.builder()
                        .fieldName(name)
                        .searchRule(Map.of(rule.getName(), value))
                        .build() : null;
    }

    public List<VegadresseDTO> getVegadresse(VegadresseRequest request, Long antall) {

        if (isNull(request)) {
            request = VegadresseRequest.builder()
                    .build();
        }

        var response = pdlAdresseConsumer.sendAdressesoek(GraphQLRequest.builder()
                .query(vegAdresseQuery)
                .variables(Map.of(
                        "paging", GraphQLRequest.Paging.builder()
                                .pageNumber(0L)
                                .resultsPerPage(antall)
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
                                buildCriteria("vegadresse.fritekst", request.getFritekst(), CONTAINS),

                                buildCriteria("vegadresse.adressenavn", true, EXISTS),
                                buildCriteria("random", random.nextDouble(), RANDOM)
                        )
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())
                ))
                .build());

        return mapperFacade.mapAsList(response.getData().getSokAdresse().getHits(), VegadresseDTO.class);
    }

    public List<MatrikkeladresseDTO> getMatrikkelAdresse(MatrikkeladresseRequest request, Long antall) {

        if (isNull(request)) {
            request = MatrikkeladresseRequest.builder()
                    .build();
        }

        var response = pdlAdresseConsumer.sendAdressesoek(GraphQLRequest.builder()
                .query(matrikkelAdresseQuery)
                .variables(Map.of(
                        "paging", GraphQLRequest.Paging.builder()
                                .pageNumber(0L)
                                .resultsPerPage(antall)
                                .build(),
                        "criteria",
                        Stream.of(
                                buildCriteria("matrikkeladresse.matrikkelId", request.getMatrikkelId(), EQUALS),
                                buildCriteria("matrikkeladresse.kommunenummer", request.getKommunenummer(), EQUALS),
                                buildCriteria("matrikkeladresse.gaardsnummer", request.getGaardsnummer(), EQUALS),
                                buildCriteria("matrikkeladresse.bruksnummer", request.getBrukesnummer(), EQUALS),
                                buildCriteria("matrikkeladresse.postnummer", request.getPostnummer(), EQUALS),
                                buildCriteria("matrikkeladresse.poststed", request.getPoststed(), EQUALS),
                                buildCriteria("matrikkeladresse.tilleggsnavn", request.getTilleggsnavn(), FUZZY),

                                buildCriteria("matrikkeladresse.gaardsnummer", true, EXISTS),
                                buildCriteria("random", random.nextDouble(), RANDOM)
                        )
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())
                ))
                .build());

        return mapperFacade.mapAsList(response.getData().getSokAdresse().getHits(), MatrikkeladresseDTO.class);
    }
}
