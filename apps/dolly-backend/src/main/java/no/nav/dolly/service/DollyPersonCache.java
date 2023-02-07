package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DollyPersonCache {

    private final TpsfService tpsfService;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final PdlDataConsumer pdlDataConsumer;
    private final MapperFacade mapperFacade;

//    @SneakyThrows
//    public DollyPerson fetchIfEmpty(DollyPerson dollyPerson) {
//
//        if (dollyPerson.isTpsfMaster()) {
//
//            if (isNull(dollyPerson.getPerson(dollyPerson.getHovedperson()))) {
//                dollyPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(List.of(dollyPerson.getHovedperson())));
//            }
//
//            var person = dollyPerson.getPerson(dollyPerson.getHovedperson());
//            if (nonNull(person)) {
//
//                dollyPerson.setPartnere(person.getRelasjoner().stream()
//                        .filter(Relasjon::isPartner)
//                        .map(Relasjon::getPersonRelasjonMed)
//                        .map(Person::getIdent)
//                        .toList());
//
//                dollyPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(
//                        dollyPerson.getPartnere().stream()
//                                .filter(ident -> isNull(dollyPerson.getPerson(ident)))
//                                .toList()));
//
//                dollyPerson.setBarn(new ArrayList<>(Stream.of(
//                                person.getRelasjoner().stream()
//                                        .filter(Relasjon::isBarn)
//                                        .map(Relasjon::getPersonRelasjonMed)
//                                        .map(Person::getIdent)
//                                        .collect(Collectors.toSet()),
//                                dollyPerson.getPartnere().stream()
//                                        .map(dollyPerson::getPerson)
//                                        .filter(Objects::nonNull)
//                                        .map(Person::getRelasjoner)
//                                        .flatMap(Collection::stream)
//                                        .filter(Relasjon::isBarn)
//                                        .map(Relasjon::getPersonRelasjonMed)
//                                        .map(Person::getIdent)
//                                        .collect(Collectors.toSet()))
//                        .flatMap(Collection::stream)
//                        .collect(Collectors.toSet())));
//
//                dollyPerson.setForeldre(person.getRelasjoner().stream()
//                        .filter(Relasjon::isForelder)
//                        .map(Relasjon::getPersonRelasjonMed)
//                        .map(Person::getIdent)
//                        .toList());
//                dollyPerson.setIdenthistorikk(person.getIdentHistorikk().stream()
//                        .map(IdentHistorikk::getAliasPerson)
//                        .map(Person::getIdent)
//                        .toList());
//                dollyPerson.setVerger(person.getVergemaal().stream()
//                        .map(RsVergemaal::getVerge)
//                        .map(RsSimplePerson::getIdent)
//                        .toList());
//                dollyPerson.setFullmektige(person.getFullmakt().stream()
//                        .map(RsFullmakt::getFullmektig)
//                        .map(RsSimplePerson::getIdent)
//                        .toList());
//            }
//        }
//
//        Set<String> identer =
//                Stream.of(List.of(dollyPerson.getHovedperson()), dollyPerson.getPartnere(),
//                                dollyPerson.getBarn(), dollyPerson.getForeldre(), dollyPerson.getIdenthistorikk(),
//                                dollyPerson.getVerger(), dollyPerson.getFullmektige())
//                        .flatMap(Collection::stream)
//                        .collect(Collectors.toSet());
//
//        List<String> manglendeIdenter = identer.stream()
//                .filter(ident -> dollyPerson.getPersondetaljer().stream()
//                        .map(Person::getIdent)
//                        .noneMatch(ident2 -> ident2.equals(ident)))
//                .toList();
//
//        if (dollyPerson.isPdlfMaster()) {
//            if (isNull(dollyPerson.getPdlfPerson())) {
//                dollyPerson.setPdlfPerson(pdlDataConsumer.getPersoner(List.of(dollyPerson.getHovedperson())).block()
//                        .stream().findFirst().orElse(new FullPersonDTO()));
//            }
//            dollyPerson.setPersondetaljer(mapperFacade.mapAsList(Stream.of(
//                                    List.of(dollyPerson.getPdlfPerson().getPerson()),
//                                    dollyPerson.getPdlfPerson().getRelasjoner().stream()
//                                            .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
//                                            .toList())
//                            .flatMap(Collection::stream)
//                            .toList(),
//                    Person.class));
//        }
//
//        if (!manglendeIdenter.isEmpty()) {
//            if (dollyPerson.isTpsfMaster()) {
//                dollyPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(manglendeIdenter));
//            } else if (dollyPerson.isPdlMaster()) {
//                var pdlPersonBolk =
//                        pdlPersonConsumer.getPdlPersoner(manglendeIdenter)
//                                .collectList()
//                                .block();
//
//                if (!pdlPersonBolk.isEmpty()) {
//                    dollyPerson.getPersondetaljer().addAll(mapperFacade.mapAsList(pdlPersonBolk.get(0).getData().getHentPersonBolk(), Person.class));
//                }
//            }
//        }
//        return dollyPerson;
//    }
//
//    public DollyPerson prepareTpsPerson(String ident, List<Tags> tags) {
//        return DollyPerson.builder()
//                .hovedperson(ident)
//                .master(Master.TPSF)
//                .tags(tags)
//                .build();
//    }
//
//    public DollyPerson prepareTpsPersoner(Person person, List<Tags> tags) {
//
//        var personer = new ArrayList<Person>();
//        personer.add(person);
//
//        return fetchIfEmpty(DollyPerson.builder()
//                .hovedperson(person.getIdent())
//                .persondetaljer(personer)
//                .master(Master.TPSF)
//                .tags(tags)
//                .build());
//    }
//
//    public DollyPerson preparePdlPersoner(PdlPerson pdlPerson) {
//
//        return DollyPerson.builder()
//                .hovedperson(pdlPerson.getData().getHentPerson().getFolkeregisteridentifikator().stream()
//                        .filter(ident -> !ident.getMetadata().isHistorisk())
//                        .map(PdlPerson.Folkeregisteridentifikator::getIdentifikasjonsnummer)
//                        .findFirst().orElse(null))
//                .partnere(pdlPerson.getData().getHentPerson().getSivilstand().stream()
//                        .filter(sivilstand -> !sivilstand.getMetadata().isHistorisk() &&
//                                nonNull(sivilstand.getRelatertVedSivilstand()))
//                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
//                        .toList())
//                .barn(pdlPerson.getData().getHentPerson().getForelderBarnRelasjon().stream()
//                        .filter(relasjon -> !relasjon.getMetadata().isHistorisk() && relasjon.isBarn())
//                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
//                        .filter(Objects::nonNull)
//                        .toList())
//                .foreldre(pdlPerson.getData().getHentPerson().getForelderBarnRelasjon().stream()
//                        .filter(relasjon -> !relasjon.getMetadata().isHistorisk() && relasjon.isForelder())
//                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
//                        .filter(Objects::nonNull)
//                        .toList())
//                .identhistorikk(pdlPerson.getData().getHentPerson().getFolkeregisteridentifikator().stream()
//                        .filter(ident -> ident.getMetadata().isHistorisk())
//                        .map(PdlPerson.Folkeregisteridentifikator::getIdentifikasjonsnummer)
//                        .toList())
//                .master(Master.PDL)
//                .tags(List.of(Tags.DOLLY))
//                .build();
//    }
//
//    public DollyPerson preparePdlfPerson(FullPersonDTO pdlfPerson, List<Tags> tags) {
//
//        return DollyPerson.builder()
//                .hovedperson(pdlfPerson.getPerson().getIdent())
//                .pdlfPerson(pdlfPerson)
//                .master(Master.PDLF)
//                .tags(tags)
//                .build();
//    }

    public DollyPerson preparePerson(Testident testident, List<Tags> tags) {

        return DollyPerson.builder()
                .ident(testident.getIdent())
                .master(testident.getMaster())
                .tags(tags)
                .build();
    }
}
