package no.nav.testnav.apps.tpsmessagingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.apps.tpsmessagingservice.consumer.ServicerutineConsumer;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServiceRutine;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineRequest;
import no.nav.testnav.apps.tpsmessagingservice.exception.BadRequestException;
import no.nav.testnav.apps.tpsmessagingservice.utils.ServiceRutineUtil;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.RelasjonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SivilstandDTO;
import no.nav.tps.ctg.s610.domain.PersondataFraTpsS610Type;
import no.nav.tps.ctg.s610.domain.RelasjonType;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.testnav.apps.tpsmessagingservice.mapper.S610PersonMappingStrategy.getSivilstand;
import static no.nav.testnav.apps.tpsmessagingservice.mapper.S610PersonMappingStrategy.getTimestamp;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.RelasjonDTO.ROLLE.FAR;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.RelasjonDTO.ROLLE.MOR;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.RelasjonDTO.ROLLE.PARTNER;
import static no.nav.tps.ctg.s610.domain.RelasjonType.EKTE;
import static no.nav.tps.ctg.s610.domain.RelasjonType.ENKE;
import static no.nav.tps.ctg.s610.domain.RelasjonType.GJPA;
import static no.nav.tps.ctg.s610.domain.RelasjonType.GLAD;
import static no.nav.tps.ctg.s610.domain.RelasjonType.REPA;
import static no.nav.tps.ctg.s610.domain.RelasjonType.SEPA;
import static no.nav.tps.ctg.s610.domain.RelasjonType.SEPR;
import static no.nav.tps.ctg.s610.domain.RelasjonType.SKIL;
import static no.nav.tps.ctg.s610.domain.RelasjonType.SKPA;

@Slf4j
@Service
public class PersonService {

    private static final String STATUS_OK = "00";
    private static final String STATUS_WARN = "04";
    private static final String PERSON_KERNINFO_SERVICE_ROUTINE = "FS03-FDNUMMER-KERNINFO-O";

    private final ServicerutineConsumer servicerutineConsumer;
    private final JAXBContext requestContext;
    private final MiljoerService miljoerService;
    private final ObjectMapper objectMapper;
    private final MapperFacade mapperFacade;

    public PersonService(ServicerutineConsumer servicerutineConsumer, ObjectMapper objectMapper, MapperFacade mapperFacade, MiljoerService miljoerService) throws JAXBException {
        this.servicerutineConsumer = servicerutineConsumer;
        this.objectMapper = objectMapper;
        this.mapperFacade = mapperFacade;
        this.miljoerService = miljoerService;
        this.requestContext = JAXBContext.newInstance(TpsServicerutineRequest.class);
    }

    private static boolean isGift(RelasjonType relasjonType) {

        return EKTE == relasjonType ||
                ENKE == relasjonType ||
                SKIL == relasjonType ||
                SEPR == relasjonType ||
                REPA == relasjonType ||
                SEPA == relasjonType ||
                SKPA == relasjonType ||
                GJPA == relasjonType ||
                GLAD == relasjonType;
    }

    private static Map<String, Object> buildRequest(String ident, String environment) {

        return new HashMap<>(Map.of(
                "fnr", ident,
                "aksjonsKode", "D1",
                "environment", environment));
    }

    private static String mapRelasjonType(RelasjonType relasjonType) {

        switch (relasjonType) {
            case MORA:
                return MOR.name();
            case FARA:
                return FAR.name();
            case EKTE:
            case ENKE:
            case SKIL:
            case SEPR:
            case REPA:
            case SEPA:
            case SKPA:
            case GJPA:
            case GLAD:
                return PARTNER.name();
            default:
                return relasjonType.name();
        }
    }

    private Map<String, PersonDTO> buildMiljoePersonWithRelasjon(Map<String, PersonRelasjon> personRelasjon) {

        return personRelasjon.entrySet().parallelStream()
                .map(entry -> PersonMiljoe.builder()
                        .miljoe(entry.getKey())
                        .person(buildPersonWithRelasjon(entry.getValue()))
                        .build())
                .collect(Collectors.toMap(PersonMiljoe::getMiljoe, PersonMiljoe::getPerson));
    }

    private void mapSivilstand(List<PersondataFraTpsS610Type> tpsFamilie, Map<String, PersonDTO> familie) {

        tpsFamilie.forEach(person ->
                familie.get(person.getPerson().getFodselsnummer()).getSivilstander().addAll(
                        nonNull(person.getPerson().getBruker().getRelasjoner()) &&
                                person.getPerson().getBruker().getRelasjoner().getRelasjon().stream()
                                        .anyMatch(relasjon -> isGift(relasjon.getTypeRelasjon())) ?
                                List.of(SivilstandDTO.builder()
                                        .sivilstand(getSivilstand(person.getPerson()))
                                        .sivilstandRegdato(getTimestamp(person.getPerson().getSivilstandDetalj().getDatoSivilstand()))
                                        .person(familie.get(person.getPerson().getFodselsnummer()))
                                        .personRelasjonMed(familie.get(person.getPerson().getBruker().getRelasjoner().getRelasjon().stream()
                                                .filter(relasjon -> isGift(relasjon.getTypeRelasjon()))
                                                .findFirst().get().getFnrRelasjon()))
                                        .build()) :
                                emptyList()));
    }

    private PersonDTO buildPersonWithRelasjon(PersonRelasjon personRelasjon) {

        var tpsFamilie = Stream.of(List.of(personRelasjon.getHovedperson()), personRelasjon.getRelasjoner())
                .flatMap(Collection::stream).toList();

        Map<String, PersonDTO> familie = tpsFamilie.parallelStream()
                .map(person -> mapperFacade.map(person.getPerson(), PersonDTO.class))
                .collect(Collectors.toMap(PersonDTO::getIdent, person -> person));

        tpsFamilie.forEach(person ->
                familie.get(person.getPerson().getFodselsnummer()).getRelasjoner().addAll(
                        nonNull(person.getPerson().getBruker().getRelasjoner()) ?
                                person.getPerson().getBruker().getRelasjoner().getRelasjon().stream()
                                        .filter(relasjon ->
                                                tpsFamilie.stream().anyMatch(person1 ->
                                                        relasjon.getFnrRelasjon().equals(person1.getPerson().getFodselsnummer())))
                                        .map(relasjon -> RelasjonDTO.builder()
                                                .relasjonTypeNavn(mapRelasjonType(relasjon.getTypeRelasjon()))
                                                .personRelasjonMed(familie.get(relasjon.getFnrRelasjon()))
                                                .person(familie.get(person.getPerson().getFodselsnummer()))
                                                .build())
                                        .collect(Collectors.toList()) : emptyList()));

        mapSivilstand(tpsFamilie, familie);

        return familie.get(personRelasjon.getHovedperson().getPerson().getFodselsnummer());
    }

    private Map<String, PersonRelasjon> getRelasjoner(Map<String, PersondataFraTpsS610Type> tpsPerson) {

        return tpsPerson.entrySet().parallelStream()
                .map(entry -> PersonRelasjonMiljoe.builder()
                        .miljoe(entry.getKey())
                        .personRelasjon(getRelasjoner(entry.getKey(), entry.getValue()))
                        .build())
                .collect(Collectors.toMap(PersonRelasjonMiljoe::getMiljoe, PersonRelasjonMiljoe::getPersonRelasjon));
    }

    private PersonRelasjon getRelasjoner(String miljoe, PersondataFraTpsS610Type tpsPerson) {

        return PersonRelasjon.builder()
                .relasjoner(nonNull(tpsPerson.getPerson().getBruker().getRelasjoner()) ?
                        tpsPerson.getPerson().getBruker().getRelasjoner().getRelasjon().parallelStream()
                                .map(relasjon -> {
                                    try {
                                        return readFromTps(relasjon.getFnrRelasjon(), List.of(miljoe)).get(miljoe);
                                    } catch (JAXBException e) {
                                        log.error(e.getMessage(), e);
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()) :
                        emptyList())
                .hovedperson(tpsPerson)
                .build();
    }

    private Map<String, PersondataFraTpsS610Type> readFromTps(String ident, List<String> miljoer) throws JAXBException {
        var request = TpsServicerutineRequest.builder()
                .tpsServiceRutine(TpsServiceRutine.builder()
                        .serviceRutinenavn(PERSON_KERNINFO_SERVICE_ROUTINE)
                        .fnr(ident)
                        .aksjonsKode("D")
                        .aksjonsKode2("1")
                        .build())
                .build();

        var xmlRequest = ServiceRutineUtil.marshallToXML(requestContext, request);
        var miljoerResponse = servicerutineConsumer.sendMessage(xmlRequest, miljoer);

        miljoerResponse.entrySet().stream()
                .forEach(entry -> log.info("Miljø: {} XML: {}", entry.getKey(), entry.getValue()));

        return miljoerResponse.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(),
                        entry -> objectMapper.convertValue(entry.getValue(), PersondataFraTpsS610Type.class)));
    }

    public Map<String, PersonDTO> getPerson(String ident, List<String> miljoer) {

        if (miljoer.isEmpty()) {
            miljoer = miljoerService.getMiljoer();
        }

        try {
            var tpsPersoner = readFromTps(ident, miljoer);

            var relasjoner = getRelasjoner(tpsPersoner);

            return !tpsPersoner.isEmpty() ? buildMiljoePersonWithRelasjon(relasjoner) : Collections.emptyMap();

        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }

        throw new BadRequestException("Endepunkt er ennå ikke støttet, beklager dette! ");
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PersonRelasjon {

        private PersondataFraTpsS610Type hovedperson;
        private List<PersondataFraTpsS610Type> relasjoner;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PersonRelasjonMiljoe {

        private String miljoe;
        private PersonRelasjon personRelasjon;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PersonMiljoe {

        private String miljoe;
        private PersonDTO person;
    }
}
