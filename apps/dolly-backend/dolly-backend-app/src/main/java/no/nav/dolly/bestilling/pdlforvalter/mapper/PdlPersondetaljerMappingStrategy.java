package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.Kjoenn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDoedsfall;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKjoenn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpprettPerson;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlStatsborgerskap;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Statsborgerskap;
import no.nav.dolly.domain.resultset.tpsf.adresse.IdentHistorikk;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.FORTROLIG;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.STRENGT_FORTROLIG_UTLAND;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.UGRADERT;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class PdlPersondetaljerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlOpprettPerson.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlOpprettPerson pdlOpprettPerson, MappingContext context) {

                        pdlOpprettPerson.setOpprettetIdent(person.getIdent());
                        pdlOpprettPerson.setHistoriskeIdenter(person.getIdentHistorikk().stream()
                                .map(IdentHistorikk::getAliasPerson)
                                .map(Person::getIdent).collect(Collectors.toList()));
                    }
                })
                .register();

        factory.classMap(Person.class, PdlNavn.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlNavn pdlNavn, MappingContext context) {

                        pdlNavn.setKilde(CONSUMER);
                        pdlNavn.setMaster(Master.FREG);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Person.class, PdlKjoenn.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlKjoenn pdlKjoenn, MappingContext context) {

                        pdlKjoenn.setKjoenn(Kjoenn.decode(person.getKjonn()));
                        pdlKjoenn.setKilde(CONSUMER);
                        pdlKjoenn.setMaster(Master.FREG);
                    }
                })
                .register();

        factory.classMap(Statsborgerskap.class, PdlStatsborgerskap.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Statsborgerskap statsborgerskap, PdlStatsborgerskap pdlStatsborgerskap, MappingContext context) {

                        pdlStatsborgerskap.setLandkode(isNotBlank(statsborgerskap.getStatsborgerskap()) ? statsborgerskap.getStatsborgerskap() : "NOR");
                        pdlStatsborgerskap.setGyldigFraOgMed(getDato(statsborgerskap.getStatsborgerskapRegdato()));
                        pdlStatsborgerskap.setGyldigTilOgMed(getDato(statsborgerskap.getStatsborgerskapTildato()));
                        pdlStatsborgerskap.setKilde(CONSUMER);
                        pdlStatsborgerskap.setMaster(Master.FREG);
                    }
                })
                .register();

        factory.classMap(Person.class, PdlAdressebeskyttelse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlAdressebeskyttelse adressebeskyttelse, MappingContext context) {

                        if ("SPSF".equals(person.getSpesreg())) {
                            adressebeskyttelse.setGradering(STRENGT_FORTROLIG);
                            adressebeskyttelse.setMaster(Master.FREG);
                        } else if ("SPFO".equals(person.getSpesreg())) {
                            adressebeskyttelse.setGradering(FORTROLIG);
                            adressebeskyttelse.setMaster(Master.FREG);
                        } else if ("SFU".equals(person.getSpesreg())) {
                            adressebeskyttelse.setGradering(STRENGT_FORTROLIG_UTLAND);
                            adressebeskyttelse.setMaster(Master.PDL);
                        } else {
                            adressebeskyttelse.setGradering(UGRADERT);
                            adressebeskyttelse.setMaster(Master.FREG);
                        }

                        adressebeskyttelse.setKilde(CONSUMER);
                    }
                })
                .register();

        factory.classMap(Person.class, PdlDoedsfall.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlDoedsfall pdlDoedsfall, MappingContext context) {

                        pdlDoedsfall.setKilde(CONSUMER);
                        pdlDoedsfall.setMaster(Master.FREG);
                    }
                })
                .byDefault()
                .register();
    }

    public static LocalDate getDato(LocalDateTime dateTime) {

        return nonNull(dateTime) ? dateTime.toLocalDate() : null;
    }
}