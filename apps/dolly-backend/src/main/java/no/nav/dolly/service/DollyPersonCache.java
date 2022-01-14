package no.nav.dolly.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Testident.Master;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.RsFullmakt;
import no.nav.dolly.domain.resultset.tpsf.RsSimplePerson;
import no.nav.dolly.domain.resultset.tpsf.RsVergemaal;
import no.nav.dolly.domain.resultset.tpsf.adresse.IdentHistorikk;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class DollyPersonCache {

    private final TpsfService tpsfService;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final ObjectMapper objectMapper;
    private final MapperFacade mapperFacade;

    @SneakyThrows
    public DollyPerson fetchIfEmpty(DollyPerson dollyPerson) {

        if (dollyPerson.isTpsfMaster()) {

            if (isNull(dollyPerson.getPerson(dollyPerson.getHovedperson()))) {
                dollyPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(List.of(dollyPerson.getHovedperson())));
            }

            var person = dollyPerson.getPerson(dollyPerson.getHovedperson());
            dollyPerson.setPartnere(person.getRelasjoner().stream()
                    .filter(Relasjon::isPartner)
                    .map(Relasjon::getPersonRelasjonMed)
                    .map(Person::getIdent)
                    .collect(Collectors.toList()));

            dollyPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(
                    dollyPerson.getPartnere().stream()
                            .filter(ident -> isNull(dollyPerson.getPerson(ident)))
                            .collect(Collectors.toList())));

            dollyPerson.setBarn(new ArrayList<>(Stream.of(
                    person.getRelasjoner().stream()
                            .filter(Relasjon::isBarn)
                            .map(Relasjon::getPersonRelasjonMed)
                            .map(Person::getIdent)
                            .collect(Collectors.toSet()),
                    dollyPerson.getPartnere().stream()
                            .map(dollyPerson::getPerson)
                            .filter(Objects::nonNull)
                            .map(Person::getRelasjoner)
                            .flatMap(Collection::stream)
                            .filter(Relasjon::isBarn)
                            .map(Relasjon::getPersonRelasjonMed)
                            .map(Person::getIdent)
                            .collect(Collectors.toSet()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet())));

            dollyPerson.setForeldre(person.getRelasjoner().stream()
                    .filter(Relasjon::isForelder)
                    .map(Relasjon::getPersonRelasjonMed)
                    .map(Person::getIdent)
                    .collect(Collectors.toList()));
            dollyPerson.setIdenthistorikk(person.getIdentHistorikk().stream()
                    .map(IdentHistorikk::getAliasPerson)
                    .map(Person::getIdent)
                    .collect(Collectors.toList()));
            dollyPerson.setVerger(person.getVergemaal().stream()
                    .map(RsVergemaal::getVerge)
                    .map(RsSimplePerson::getIdent)
                    .collect(Collectors.toList()));
            dollyPerson.setFullmektige(person.getFullmakt().stream()
                    .map(RsFullmakt::getFullmektig)
                    .map(RsSimplePerson::getIdent)
                    .collect(Collectors.toList()));
        }

        Set<String> identer =
                Stream.of(List.of(dollyPerson.getHovedperson()), dollyPerson.getPartnere(),
                        dollyPerson.getBarn(), dollyPerson.getForeldre(), dollyPerson.getIdenthistorikk(),
                        dollyPerson.getVerger(), dollyPerson.getFullmektige())
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());

        List<String> manglendeIdenter = identer.stream()
                .filter(ident -> dollyPerson.getPersondetaljer().stream()
                        .map(Person::getIdent)
                        .noneMatch(ident2 -> ident2.equals(ident)))
                .collect(Collectors.toList());

        if (!manglendeIdenter.isEmpty()) {
            if (dollyPerson.isTpsfMaster()) {
                dollyPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(manglendeIdenter));
            } else if (dollyPerson.isPdlMaster()) {
                PdlPersonBolk pdlPersonBolk = objectMapper.readValue(
                        pdlPersonConsumer.getPdlPersoner(manglendeIdenter).toString(),
                        PdlPersonBolk.class);
                dollyPerson.getPersondetaljer().addAll(mapperFacade.mapAsList(pdlPersonBolk.getData().getHentPersonBolk(), Person.class));
            } else if (dollyPerson.isPdlfMaster()) {

            }
        }

        return dollyPerson;
    }

    public DollyPerson prepareTpsPerson(String ident) {
        return DollyPerson.builder()
                .hovedperson(ident)
                .master(Master.TPSF)
                .build();
    }

    public DollyPerson prepareTpsPersoner(Person person) {

        var personer = new ArrayList<Person>();
        personer.add(person);

        return fetchIfEmpty(DollyPerson.builder()
                .hovedperson(person.getIdent())
                .persondetaljer(personer)
                .master(Master.TPSF)
                .build());
    }

    public DollyPerson preparePdlPersoner(PdlPerson pdlPerson) {

        return DollyPerson.builder()
                .hovedperson(pdlPerson.getData().getHentPerson().getFolkeregisteridentifikator().stream()
                        .filter(ident -> !ident.getMetadata().isHistorisk())
                        .map(PdlPerson.Folkeregisteridentifikator::getIdentifikasjonsnummer)
                        .findFirst().orElse(null))
                .partnere(pdlPerson.getData().getHentPerson().getSivilstand().stream()
                        .filter(sivilstand -> !sivilstand.getMetadata().isHistorisk() &&
                                nonNull(sivilstand.getRelatertVedSivilstand()))
                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                        .collect(Collectors.toList()))
                .barn(pdlPerson.getData().getHentPerson().getForelderBarnRelasjon().stream()
                        .filter(relasjon -> !relasjon.getMetadata().isHistorisk() && relasjon.isBarn())
                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                        .collect(Collectors.toList()))
                .foreldre(pdlPerson.getData().getHentPerson().getForelderBarnRelasjon().stream()
                        .filter(relasjon -> !relasjon.getMetadata().isHistorisk() && relasjon.isForelder())
                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                        .collect(Collectors.toList()))
                .identhistorikk(pdlPerson.getData().getHentPerson().getFolkeregisteridentifikator().stream()
                        .filter(ident -> ident.getMetadata().isHistorisk())
                        .map(PdlPerson.Folkeregisteridentifikator::getIdentifikasjonsnummer)
                        .collect(Collectors.toList()))
                .master(Master.PDL)
                .build();
    }

    public DollyPerson preparePdlfPerson(FullPersonDTO pdlfPerson) {

        return DollyPerson.builder()
                .hovedperson(pdlfPerson.getPerson().getIdent())
                .pdlfPerson(pdlfPerson)
                .master(Master.PDLF)
                .build();
    }
}
