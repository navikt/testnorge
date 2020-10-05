package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.FORTROLIG;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.UGRADERT;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.Kjoenn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDoedsfall;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFoedsel;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKjoenn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpprettPerson;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlStatsborgerskap;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Statsborgerskap;
import no.nav.dolly.domain.resultset.tpsf.adresse.IdentHistorikk;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlPersondetaljerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlOpprettPerson.class)
                .customize(new CustomMapper<Person, PdlOpprettPerson>() {
                    @Override
                    public void mapAtoB(Person person, PdlOpprettPerson pdlOpprettPerson, MappingContext context) {

                        pdlOpprettPerson.setOpprettetIdent(person.getIdent());
                        pdlOpprettPerson.setHistoriskeIdenter(person.getIdentHistorikk().stream().map(IdentHistorikk::getIdent).collect(Collectors.toList()));
                    }
                })
                .register();

        factory.classMap(Person.class, PdlFoedsel.class)
                .customize(new CustomMapper<Person, PdlFoedsel>() {
                    @Override
                    public void mapAtoB(Person person, PdlFoedsel pdlFoedsel, MappingContext context) {

                        pdlFoedsel.setKilde(CONSUMER);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Person.class, PdlNavn.class)
                .customize(new CustomMapper<Person, PdlNavn>() {
                    @Override
                    public void mapAtoB(Person person, PdlNavn pdlNavn, MappingContext context) {

                        pdlNavn.setKilde(CONSUMER);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Person.class, PdlKjoenn.class)
                .customize(new CustomMapper<Person, PdlKjoenn>() {
                    @Override
                    public void mapAtoB(Person person, PdlKjoenn pdlKjoenn, MappingContext context) {

                        pdlKjoenn.setKjoenn(Kjoenn.decode(person.getKjonn()));
                        pdlKjoenn.setKilde(CONSUMER);
                    }
                })
                .register();

        factory.classMap(Statsborgerskap.class, PdlStatsborgerskap.class)
                .customize(new CustomMapper<Statsborgerskap, PdlStatsborgerskap>() {
                    @Override
                    public void mapAtoB(Statsborgerskap statsborgerskap, PdlStatsborgerskap pdlStatsborgerskap, MappingContext context) {

                        pdlStatsborgerskap.setLandkode(isNotBlank(statsborgerskap.getStatsborgerskap()) ? statsborgerskap.getStatsborgerskap() : "NOR");
                        pdlStatsborgerskap.setGyldigFom(getDato(statsborgerskap.getStatsborgerskapRegdato()));
                        pdlStatsborgerskap.setKilde(CONSUMER);
                    }
                })
                .register();

        factory.classMap(Person.class, PdlAdressebeskyttelse.class)
                .customize(new CustomMapper<Person, PdlAdressebeskyttelse>() {
                    @Override
                    public void mapAtoB(Person person, PdlAdressebeskyttelse adressebeskyttelse, MappingContext context) {

                        if ("SPSF".equals(person.getSpesreg())) {
                            adressebeskyttelse.setGradering(STRENGT_FORTROLIG);

                        } else if ("SPFO".equals(person.getSpesreg())) {
                            adressebeskyttelse.setGradering(FORTROLIG);

                        } else {
                            adressebeskyttelse.setGradering(UGRADERT);
                        }

                        adressebeskyttelse.setKilde(CONSUMER);
                    }
                })
                .register();

        factory.classMap(Person.class, PdlDoedsfall.class)
                .customize(new CustomMapper<Person, PdlDoedsfall>() {
                    @Override
                    public void mapAtoB(Person person, PdlDoedsfall pdlDoedsfall, MappingContext context) {

                        pdlDoedsfall.setKilde(CONSUMER);
                    }
                })
                .byDefault()
                .register();
    }

    public static LocalDate getDato(LocalDateTime dateTime) {

        return nonNull(dateTime) ? dateTime.toLocalDate() : null;
    }
}