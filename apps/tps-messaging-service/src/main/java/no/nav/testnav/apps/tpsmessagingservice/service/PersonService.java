package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.ServicerutineConsumer;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineRequest;
import no.nav.testnav.apps.tpsmessagingservice.utils.ServiceRutineUtil;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.tps.ctg.s610.domain.S610PersonType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getErrorStatus;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
public class PersonService {

    private static final String PERSON_KERNINFO_SERVICE_ROUTINE = "FS03-FDNUMMER-KERNINFO-O";

    private final ServicerutineConsumer servicerutineConsumer;
    private final JAXBContext requestContext;
    private final JAXBContext responseContext;
    private final MiljoerService miljoerService;

    public PersonService(ServicerutineConsumer servicerutineConsumer, MiljoerService miljoerService) throws JAXBException {
        this.servicerutineConsumer = servicerutineConsumer;
        this.miljoerService = miljoerService;
        this.requestContext = JAXBContext.newInstance(TpsServicerutineRequest.class);
        this.responseContext = JAXBContext.newInstance(S610PersonType.class);
    }

    private S610PersonType unmarshallFromXml(String endringsmeldingResponse) throws JAXBException {

        if (isNotBlank(endringsmeldingResponse)) {

            var unmarshaller = responseContext.createUnmarshaller();
            var reader = new StringReader(endringsmeldingResponse);

            return (S610PersonType) unmarshaller.unmarshal(reader);

        } else {

            return null;
        }
    }

    public Map<String, PersonDTO> getPerson(String ident, List<String> miljoer) {

        if (miljoer.isEmpty()) {
            miljoer = miljoerService.getMiljoer();
        }

        try {
            var request = TpsServicerutineRequest.builder()
                    .tpsServiceRutine(TpsServicerutineRequest.TpsServiceRutine.builder()
                            .serviceRutinenavn(PERSON_KERNINFO_SERVICE_ROUTINE)
                            .fnr(ident)
                            .aksjonsKode("D")
                            .aksjonsKode2("1")
                            .build())
                    .build();

            var xmlRequest = ServiceRutineUtil.marshallToXML(requestContext, request);
            var miljoerResponse = servicerutineConsumer.sendMessage(xmlRequest, miljoer);

            miljoerResponse.entrySet().stream()
                    .collect(Collectors.toMap(Entry::getKey, entry -> {

                        try {
                            var tpsPerson = unmarshallFromXml(entry.getValue());

                            Map<String, PersonRelasjon> relasjoner = getRelasjoner(miljoePerson);

                            return !miljoePerson.isEmpty() ? buildMiljoePersonWithRelasjon(relasjoner) : emptyMap();
                        } catch (JAXBException e) {
                            log.error(e.getMessage(), e);
                            return getErrorStatus(e);
                        }
                        return null;
                    }));

        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }



    private void attachReleasjoner(List<PersonRelasjonDiv> personRelasjoner) {

        personRelasjoner.forEach(personRelasjon -> {
            personRelasjon.getPerson().setSivilstander(personRelasjon.getSivilstander());
            personRelasjon.getPerson().setRelasjoner(personRelasjon.getRelasjoner());
        });
    }

    private List<PersonRelasjonDiv> detachReleasjoner(Person person) {

        List<Person> personer = new ArrayList<>();
        Stream.of(singletonList(person), person.getRelasjoner().stream()
                .map(Relasjon::getPersonRelasjonMed)
                .collect(Collectors.toList())).forEach(personer::addAll);

        List<PersonRelasjonDiv> personRelasjoner = personer.stream().map(person1 ->
                        PersonRelasjonDiv.builder()
                                .person(person1)
                                .relasjoner(person1.getRelasjoner())
                                .sivilstander(person1.getSivilstander())
                                .build())
                .collect(Collectors.toList());

        personRelasjoner.forEach(personRelasjon -> {
            personRelasjon.getPerson().setSivilstander(null);
            personRelasjon.getPerson().setRelasjoner(null);
        });

        return personRelasjoner;
    }

    private Map<String, Person> buildMiljoePersonWithRelasjon(Map<String, PersonRelasjon> personRelasjon) {

        return personRelasjon.entrySet().parallelStream()
                .map(entry -> PersonMiljoe.builder()
                        .miljoe(entry.getKey())
                        .person(buildPersonWithRelasjon(entry.getValue()))
                        .build())
                .collect(Collectors.toMap(PersonMiljoe::getMiljoe, PersonMiljoe::getPerson));
    }

    private Person buildPersonWithRelasjon(PersonRelasjon personRelasjon) {

        List<S610PersonType> tpsFamilie = new ArrayList<>();
        Stream.of(singletonList(personRelasjon.getHovedperson()), personRelasjon.getRelasjoner()).forEach(tpsFamilie::addAll);

        Map<String, Person> familie = tpsFamilie.parallelStream()
                .map(person -> mapperFacade.map(person, Person.class))
                .collect(Collectors.toMap(Person::getIdent, person -> person));

        tpsFamilie.forEach(person ->
                familie.get(person.getFodselsnummer()).getRelasjoner().addAll(
                        nonNull(person.getBruker().getRelasjoner()) ?
                                person.getBruker().getRelasjoner().getRelasjon().stream()
                                        .filter(relasjon ->
                                                tpsFamilie.stream().anyMatch(person1 ->
                                                        relasjon.getFnrRelasjon().equals(person1.getFodselsnummer())))
                                        .map(relasjon -> Relasjon.builder()
                                                .relasjonTypeNavn(mapRelasjonType(relasjon.getTypeRelasjon()))
                                                .personRelasjonMed(familie.get(relasjon.getFnrRelasjon()))
                                                .person(familie.get(person.getFodselsnummer()))
                                                .build())
                                        .collect(Collectors.toList()) : emptyList()));

        mapSivilstand(tpsFamilie, familie);

        return familie.get(personRelasjon.getHovedperson().getFodselsnummer());
    }

    private void mapSivilstand(List<S610PersonType> tpsFamilie, Map<String, Person> familie) {

        tpsFamilie.forEach(person ->
                familie.get(person.getFodselsnummer()).getSivilstander().addAll(
                        nonNull(person.getBruker().getRelasjoner()) &&
                                person.getBruker().getRelasjoner().getRelasjon().stream()
                                        .anyMatch(relasjon -> isGift(relasjon.getTypeRelasjon())) ?
                                singletonList(Sivilstand.builder()
                                        .sivilstand(getSivilstand(person))
                                        .sivilstandRegdato(getTimestamp(person.getSivilstandDetalj().getDatoSivilstand()))
                                        .person(familie.get(person.getFodselsnummer()))
                                        .personRelasjonMed(familie.get(person.getBruker().getRelasjoner().getRelasjon().stream()
                                                .filter(relasjon -> isGift(relasjon.getTypeRelasjon()))
                                                .findFirst().get().getFnrRelasjon()))
                                        .build()) :
                                emptyList()));
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

    private TpsPersonMiljoe readFromTps(String ident, String environment) {

        try {
            TpsServiceRoutineResponse response = tpsServiceRoutineService.execute(PERSON_KERNINFO_SERVICE_ROUTINE,
                    buildRequest(ident, environment), true);

            if (isStatusOK(((ResponseStatus) ((Map) response.getResponse()).get(STATUS)))) {
                return TpsPersonMiljoe.builder()
                        .person(objectMapper.convertValue(((Map) response.getResponse()).get("data1"), S610PersonType.class))
                        .miljoe(environment)
                        .build();
            } else {
                return TpsPersonMiljoe.builder()
                        .errorMsg(((ResponseStatus) ((Map) response.getResponse()).get(STATUS)).getUtfyllendeMelding())
                        .miljoe(environment)
                        .build();
            }

        } catch (Exception e) {
            log.error(MILJOE_IKKE_FUNNET, environment, e);
            return TpsPersonMiljoe.builder()
                    .errorMsg(e.getMessage())
                    .miljoe(environment)
                    .build();
        }
    }

    private Map<String, PersonRelasjon> getRelasjoner(Map<String, S610PersonType> tpsPerson) {

        return tpsPerson.entrySet().parallelStream()
                .map(entry -> PersonRelasjonMiljoe.builder()
                        .miljoe(entry.getKey())
                        .personRelasjon(getRelasjoner(entry.getValue(), entry.getKey()))
                        .build())
                .collect(Collectors.toMap(PersonRelasjonMiljoe::getMiljoe, PersonRelasjonMiljoe::getPersonRelasjon));
    }

    private PersonRelasjon getRelasjoner(S610PersonType tpsPerson, String miljoe) {

        return PersonRelasjon.builder()
                .relasjoner(nonNull(tpsPerson.getBruker().getRelasjoner()) ?
                        tpsPerson.getBruker().getRelasjoner().getRelasjon().parallelStream()
                                .map(relasjon -> readFromTps(relasjon.getFnrRelasjon(), miljoe).getPerson())
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()) :
                        emptyList())
                .hovedperson(tpsPerson)
                .build();
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

    private static boolean isStatusOK(ResponseStatus status) {
        return STATUS_OK.equals(status.getKode()) || STATUS_WARN.equals(status.getKode());
    }

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
private static class PersonRelasjon {

    private S610PersonType hovedperson;
    private List<S610PersonType> relasjoner;
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
    private Person person;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
private static class TpsPersonMiljoe {

    private String miljoe;
    private S610PersonType person;
    private String errorMsg;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
private static class PersonRelasjonDiv {

    private Person person;
    private List<Sivilstand> sivilstander;
    private List<Relasjon> relasjoner;
}

}
