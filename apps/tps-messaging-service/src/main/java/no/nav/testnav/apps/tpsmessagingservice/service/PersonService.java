package no.nav.testnav.apps.tpsmessagingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.apps.tpsmessagingservice.consumer.ServicerutineConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineS610Response;
import no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil;
import no.nav.testnav.apps.tpsmessagingservice.utils.ServiceRutineUtil;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.RelasjonDTO;
import no.nav.tps.ctg.s610.domain.PersondataFraTpsS610Type;
import no.nav.tps.ctg.s610.domain.RelasjonType;
import no.nav.tps.ctg.s610.domain.S610PersonType;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.data.tpsmessagingservice.v1.RelasjonDTO.ROLLE.FAR;
import static no.nav.testnav.libs.data.tpsmessagingservice.v1.RelasjonDTO.ROLLE.MOR;
import static no.nav.testnav.libs.data.tpsmessagingservice.v1.RelasjonDTO.ROLLE.PARTNER;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class PersonService {

    private static final String STATUS_OK = "00";
    private static final String NOT_FOUND = "PERSON IKKE FUNNET";
    private static final String PERSON_KERNINFO_SERVICE_ROUTINE = "FS03-FDNUMMER-KERNINFO-O";

    private final ServicerutineConsumer servicerutineConsumer;
    private final JAXBContext requestContext;
    private final TestmiljoerServiceConsumer testmiljoerServiceConsumer;
    private final ObjectMapper objectMapper;
    private final MapperFacade mapperFacade;

    public PersonService(ServicerutineConsumer servicerutineConsumer,
                         ObjectMapper objectMapper,
                         MapperFacade mapperFacade,
                         TestmiljoerServiceConsumer testmiljoerServiceConsumer) throws JAXBException {

        this.servicerutineConsumer = servicerutineConsumer;
        this.objectMapper = objectMapper;
        this.mapperFacade = mapperFacade;
        this.testmiljoerServiceConsumer = testmiljoerServiceConsumer;
        this.requestContext = JAXBContext.newInstance(TpsServicerutineRequest.class);
    }

    @SneakyThrows
    public TpsServicerutineS610Response unmarshallFromXml(String endringsmeldingResponse) {

        if (TpsMeldingCommand.NO_RESPONSE.equals(endringsmeldingResponse)) {

            return TpsServicerutineS610Response.builder()
                    .tpsPersonData(TpsServicerutineS610Response.TpsPersonData.builder()
                            .tpsSvar(TpsServicerutineS610Response.TpsSvar.builder()
                                    .svarStatus(EndringsmeldingUtil.getNoAnswerStatus())
                                    .build())
                            .build())
                    .build();
        } else {

            var jsonRoot = XML.toJSONObject(endringsmeldingResponse);

            return objectMapper.readValue(jsonRoot.toString(), TpsServicerutineS610Response.class);
        }
    }

    public List<PersonMiljoeDTO> getPerson(String ident, List<String> miljoer) {

            if (miljoer.isEmpty()) {
                miljoer = testmiljoerServiceConsumer.getMiljoer()
                        .stream()
                        .filter(miljoe -> !miljoe.contains("q1") && !miljoe.contains("q2"))
                        .toList();
            }

        try {
            var tpsPersoner = readFromTps(ident, miljoer);

            var relasjoner = getRelasjoner(tpsPersoner.entrySet().stream()
                    .filter(entry -> nonNull(entry.getValue().getTpsPersonData()) &&
                            nonNull(entry.getValue().getTpsPersonData().getTpsSvar()) &&
                            isStatusOK(entry.getValue().getTpsPersonData().getTpsSvar().getSvarStatus()))
                    .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue()
                            .getTpsPersonData()
                            .getTpsSvar()
                            .getPersonDataS610()
                            .getPerson())));

            var personerMedRelasjoner = buildMiljoePersonWithRelasjon(relasjoner).entrySet().stream()
                    .map(entry -> PersonMiljoeDTO.builder()
                            .ident(ident)
                            .miljoe(entry.getKey())
                            .status("OK")
                            .person(entry.getValue())
                            .build())
                    .toList();

            var hentingMedFeil = tpsPersoner.entrySet().stream()
                    .filter(entry -> nonNull(entry.getValue().getTpsPersonData()) &&
                            nonNull(entry.getValue().getTpsPersonData().getTpsSvar()) &&
                            !isStatusOK(entry.getValue().getTpsPersonData().getTpsSvar().getSvarStatus()))
                    .map(entry -> PersonMiljoeDTO.builder()
                            .miljoe(entry.getKey())
                            .status("FEIL")
                            .melding(entry.getValue().getTpsPersonData().getTpsSvar().getSvarStatus().getReturMelding())
                            .utfyllendeMelding(entry.getValue().getTpsPersonData().getTpsSvar().getSvarStatus().getUtfyllendeMelding())
                            .build())
                    .toList();

            return Stream.of(personerMedRelasjoner, hentingMedFeil)
                    .flatMap(Collection::stream)
                    .filter(entry -> isBlank(entry.getUtfyllendeMelding()) || !NOT_FOUND.equals(entry.getUtfyllendeMelding()))
                    .toList();

        } catch (Exception e) {
            log.error("Feilet å hente data for {} i miljøer {}, melding {}", ident, miljoer, e.getMessage(), e);
            throw e;
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

    private PersonDTO buildPersonWithRelasjon(PersonRelasjon personRelasjon) {

        var tpsFamilie = Stream.of(List.of(personRelasjon.getHovedperson()), personRelasjon.getRelasjoner())
                .flatMap(Collection::stream).toList();

        Map<String, PersonDTO> familie = tpsFamilie.parallelStream()
                .map(person -> mapperFacade.map(person, PersonDTO.class))
                .collect(Collectors.toMap(PersonDTO::getIdent, person -> person));

        if (nonNull(personRelasjon.getHovedperson().getBruker().getRelasjoner())) {
            familie.get(personRelasjon.getHovedperson().getFodselsnummer()).setRelasjoner(personRelasjon.getHovedperson()
                    .getBruker().getRelasjoner().getRelasjon().stream()
                    .map(relasjon -> RelasjonDTO.builder()
                            .personRelasjonMed(familie.get(relasjon.getFnrRelasjon()))
                            .relasjonTypeNavn(mapRelasjonType(relasjon.getTypeRelasjon()))
                            .build())
                    .collect(Collectors.toList()));
        }

        return familie.get(personRelasjon.getHovedperson().getFodselsnummer());
    }

    private Map<String, PersonRelasjon> getRelasjoner(Map<String, S610PersonType> tpsPerson) {

        return tpsPerson.entrySet().parallelStream()
                .map(entry -> PersonRelasjonMiljoe.builder()
                        .miljoe(entry.getKey())
                        .personRelasjon(getRelasjoner(entry.getKey(), entry.getValue()))
                        .build())
                .collect(Collectors.toMap(PersonRelasjonMiljoe::getMiljoe, PersonRelasjonMiljoe::getPersonRelasjon));
    }

    private PersonRelasjon getRelasjoner(String miljoe, S610PersonType tpsPerson) {

        return PersonRelasjon.builder()
                .relasjoner(nonNull(tpsPerson.getBruker().getRelasjoner()) ?
                        tpsPerson.getBruker().getRelasjoner().getRelasjon().parallelStream()
                                .map(relasjon -> readFromTps(relasjon.getFnrRelasjon(), List.of(miljoe)))
                                .filter(Objects::nonNull)
                                .map(entry -> entry.get(miljoe))
                                .map(TpsServicerutineS610Response::getTpsPersonData)
                                .map(TpsServicerutineS610Response.TpsPersonData::getTpsSvar)
                                .map(TpsServicerutineS610Response.TpsSvar::getPersonDataS610)
                                .filter(Objects::nonNull)
                                .map(PersondataFraTpsS610Type::getPerson)
                                .toList() :
                        emptyList())
                .hovedperson(tpsPerson)
                .build();
    }

    private Map<String, TpsServicerutineS610Response> readFromTps(String ident, List<String> miljoer) {

        var request = TpsServicerutineRequest.builder()
                .tpsServiceRutine(TpsServicerutineRequest.TpsServiceRutine.builder()
                        .serviceRutinenavn(PERSON_KERNINFO_SERVICE_ROUTINE)
                        .fnr(ident)
                        .aksjonsKode("D")
                        .aksjonsKode2("1")
                        .build())
                .build();

        var xmlRequest = ServiceRutineUtil.marshallToXML(requestContext, request)
                .replace("</aksjonsKode2>", "</aksjonsKode2><systemId>Dolly</systemId>");

        var miljoerResponse = servicerutineConsumer.sendMessage(xmlRequest, miljoer);

        miljoerResponse.forEach((key, value) -> log.info("Miljø: {} XML: {}", key, value));

        return miljoerResponse.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey,
                        entry -> unmarshallFromXml(entry.getValue())));
    }

    private static String mapRelasjonType(RelasjonType relasjonType) {

        return switch (relasjonType) {
            case MORA -> MOR.name();
            case FARA -> FAR.name();
            case EKTE, ENKE, SKIL, SEPR, REPA, SEPA, SKPA, GJPA, GLAD -> PARTNER.name();
            default -> relasjonType.name();
        };
    }

    private static boolean isStatusOK(TpsMeldingResponse response) {

        return STATUS_OK.equals(response.getReturStatus());
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
        private PersonDTO person;
    }
}
