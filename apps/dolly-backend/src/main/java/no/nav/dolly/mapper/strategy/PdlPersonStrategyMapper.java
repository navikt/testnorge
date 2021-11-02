package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret;
import no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public final class PdlPersonStrategyMapper implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PdlPersonBolk.PersonBolk.class, Person.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlPersonBolk.PersonBolk personBolk, Person person, MappingContext context) {

                        if (personBolk.getPerson().getNavn().stream()
                                .anyMatch(personNavn -> !personNavn.getMetadata().isHistorisk())) {
                            mapperFacade.map(personBolk.getPerson().getNavn().stream()
                                    .filter(personNavn -> !personNavn.getMetadata().isHistorisk())
                                    .findFirst().get(), person);
                        }

                        person.setIdenttype(personBolk.getPerson().getFolkeregisteridentifikator().stream()
                                .filter(ident -> !ident.getMetadata().isHistorisk())
                                .map(PdlPerson.Folkeregisteridentifikator::getType)
                                .findFirst().orElse(null));

                        person.setKjonn(personBolk.getPerson().getKjoenn().stream()
                                .anyMatch(kjoenn -> !kjoenn.getMetadata().isHistorisk()) ?
                                personBolk.getPerson().getKjoenn().stream()
                                        .filter(kjoenn -> !kjoenn.getMetadata().isHistorisk())
                                        .map(PdlPerson.PdlKjoenn::getKjoenn)
                                        .findFirst().get().substring(0, 1) : null);

                        person.setFoedselsdato(personBolk.getPerson().getFoedsel().stream()
                                .anyMatch(foedsel -> !foedsel.getMetadata().isHistorisk()) ?
                                personBolk.getPerson().getFoedsel().stream()
                                        .filter(foedsel -> !foedsel.getMetadata().isHistorisk())
                                        .map(PdlPerson.Foedsel::getFoedselsdato)
                                        .findFirst().get().atStartOfDay() : null);

                        person.setDoedsdato(personBolk.getPerson().getDoedsfall().stream()
                                .anyMatch(doedsfall -> !doedsfall.getMetadata().isHistorisk()) ?
                                personBolk.getPerson().getDoedsfall().stream()
                                        .filter(doedsfall -> !doedsfall.getMetadata().isHistorisk())
                                        .map(PdlPerson.Doedsfall::getDoedsdato)
                                        .findFirst().get().atStartOfDay() : null);

                        person.getInnvandretUtvandret().addAll(
                                personBolk.getPerson().getUtflyttingFraNorge().stream()
                                        .filter(utflytting -> !utflytting.getMetadata().isHistorisk())
                                        .map(utflytting -> InnvandretUtvandret.builder()
                                                .innutvandret(InnUtvandret.UTVANDRET)
                                                .landkode(utflytting.getTilflyttingsland())
                                                .flyttedato(utflytting.getFolkeregistermetadata()
                                                        .getGyldighetstidspunkt().atStartOfDay())
                                                .build())
                                        .collect(Collectors.toList()));
                    }
                })
                .byDefault()
                .register();
    }
}
