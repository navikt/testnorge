package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.dto.HendelseIdRequest;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class HendelseIdService {

    private final PersonRepository personRepository;
    private final PdlTestdataConsumer pdlTestdataConsumer;
    private final RelasjonRepository relasjonRepository;

    public Mono<Void> oppdaterPerson(OrdreResponseDTO response) {

        return Flux.concat(Mono.just(response.getHovedperson()),
                        Flux.fromIterable(response.getRelasjoner()))
                .flatMap(personHendelse -> personRepository.findByIdent(personHendelse.getIdent())
                        .flatMapMany(dbPerson -> Flux.fromIterable(personHendelse.getOrdrer())
                                .filter(OrdreResponseDTO.PdlStatusDTO::isDataElement)
                                .flatMap(ordre -> {
                                    try {
                                        var getter = PersonDTO.class.getMethod("get" + ordre.getInfoElement().getDescription());
                                        val artifact = (List<DbVersjonDTO>) getter.invoke(dbPerson.getPerson());
                                        return Flux.fromIterable(ordre.getHendelser())
                                                .flatMap(hendelse -> Flux.fromIterable(artifact)
                                                        .filter(fact -> hendelse.getId().equals(fact.getId()))
                                                        .doOnNext(fact ->
                                                                fact.setHendelseId(hendelse.getHendelseId())));
                                    } catch (NoSuchMethodException |
                                             InvocationTargetException |
                                             IllegalAccessException e) {
                                        log.error("Feilet å lagre hendelseId, {}", e.getMessage());
                                        return Mono.empty();
                                    }
                                })
                        ))
                .collectList()
                .then();
    }

    public Flux<DbVersjonDTO> getPdlOpplysninger(String ident) {

        return personRepository.findByIdent(ident)
                .map(DbPerson::getPerson)
                .flatMapMany(person -> Flux.fromArray(person.getClass().getMethods())
                        .filter(method -> method.getName().contains("get"))
                        .flatMap(method -> {
                            try {
                                return Mono.just(method.invoke(person));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                log.error("Feilet å hente hendelseId, {}", e.getMessage());
                                return Mono.empty();
                            }
                        })
                        .filter(Objects::nonNull)
                        .filter(List.class::isInstance)
                        .map(value -> (List<DbVersjonDTO>) value)
                        .flatMap(Flux::fromIterable)
                        .filter(DbVersjonDTO::isPdlMaster)
                        .filter(dbVersjonDTO -> isNotBlank(dbVersjonDTO.getHendelseId())));
    }

    public Mono<Void> deletePdlHendelser(DbPerson person) {

        if (isTestnorgeIdent(person.getIdent())) {

            return Flux.concat(
                            getPdlOpplysninger(person.getIdent())
                                    .flatMap(opplysning -> buildHendelseRequest(person.getIdent(), opplysning)),
                            relasjonRepository.findByPersonId(person.getId())
                                    .flatMap(relasjon -> personRepository.findById(relasjon.getRelatertPersonId()))
                                    .map(DbPerson::getPerson)
                                    .flatMap(relasjonPerson -> Flux.concat(
                                            Flux.fromIterable(relasjonPerson.getSivilstand())
                                                    .map(relatert -> buildHendelseRequest(relasjonPerson.getIdent(), relatert)),
                                            Flux.fromIterable(relasjonPerson.getForelderBarnRelasjon())
                                                    .map(relatert -> buildHendelseRequest(relasjonPerson.getIdent(), relatert)),
                                            Flux.fromIterable(relasjonPerson.getFullmakt())
                                                    .map(relatert -> buildHendelseRequest(relasjonPerson.getIdent(), relatert))))
                                    .flatMap(Flux::from))
                    .flatMap(pdlTestdataConsumer::deleteHendelse)
                    .collectList()
                    .then();
        }

        return Mono.empty();
    }

    private Mono<HendelseIdRequest> buildHendelseRequest(String ident, DbVersjonDTO opplysning) {

        return Mono.just(opplysning)
                .filter(DbVersjonDTO::isPdlMaster)
                .map(type -> HendelseIdRequest.builder()
                        .ident(ident)
                        .hendelseId(type.getHendelseId())
                        .build());
    }

    public Mono<Void> deletePdlHendelse(String ident, String artifact, Integer id) {

        if (isTestnorgeIdent(ident)) {

            return personRepository.findByIdent(ident)
                    .map(DbPerson::getPerson)
                    .flatMap(person -> {
                        try {
                            val method = person.getClass().getMethod("get" + artifact);
                            return Mono.just(method.invoke(person));
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            log.error("Feilet å hente get{} fra person med ident {}", artifact, ident);
                            return Mono.empty();
                        }
                    })
                    .filter(List.class::isInstance)
                    .map(value -> (List<DbVersjonDTO>) value)
                    .flatMap(opplysninger -> Flux.fromIterable(opplysninger)
                            .filter(opplysning -> id.equals(opplysning.getId()))
                            .filter(DbVersjonDTO::isPdlMaster)
                            .map(DbVersjonDTO::getHendelseId)
                            .filter(StringUtils::isNotBlank)
                            .next()
                            .flatMap(hendelseId -> pdlTestdataConsumer.deleteHendelse(ident, hendelseId)))
                    .then();
        }
        return Mono.empty();
    }
}
