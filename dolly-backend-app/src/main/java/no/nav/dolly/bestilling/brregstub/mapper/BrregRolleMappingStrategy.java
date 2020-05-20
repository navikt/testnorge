package no.nav.dolly.bestilling.brregstub.mapper;

import static java.lang.String.format;
import static no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo.AdresseTo;
import static no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo.NavnTo;
import static no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo.RolleTo;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsMatrikkeladresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class BrregRolleMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RolleUtskriftMapper.BregPerson.class, RolleoversiktTo.class)
                .customize(new CustomMapper<RolleUtskriftMapper.BregPerson, RolleoversiktTo>() {
                    @Override
                    public void mapAtoB(RolleUtskriftMapper.BregPerson bregPerson, RolleoversiktTo rolleoversiktTo, MappingContext context) {

                        Person rollePerson = bregPerson.getTpsPerson().getPerson(bregPerson.getTpsPerson().getHovedperson());

                        rolleoversiktTo.setAdresse(mapperFacade.map(rollePerson, AdresseTo.class));
                        rolleoversiktTo.setEnheter(mapperFacade.mapAsList(bregPerson.getBregdata().getEnheter(), RolleTo.class));
                        rolleoversiktTo.setFnr(bregPerson.getTpsPerson().getHovedperson());
                        rolleoversiktTo.setFodselsdato(rollePerson.getFoedselsdato().toLocalDate());
                        rolleoversiktTo.setHovedstatus(0);
                        rolleoversiktTo.setNavn(mapperFacade.map(rollePerson, NavnTo.class));
                        rolleoversiktTo.setUnderstatuser(bregPerson.getBregdata().getUnderstatuser());
                    }
                })
                .register();

        factory.classMap(RsBregdata.RolleTo.class, RolleTo.class)
                .customize(new CustomMapper<RsBregdata.RolleTo, RolleTo>() {
                    @Override
                    public void mapAtoB(RsBregdata.RolleTo rsRolleTo, RolleTo rolleTo, MappingContext context) {

                        rolleTo.setPersonRolle(mapperFacade.mapAsList(rsRolleTo.getPersonroller(), RolleoversiktTo.RolleStatus.class));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Person.class, AdresseTo.class)
                .customize(new CustomMapper<Person, AdresseTo>() {
                    @Override
                    public void mapAtoB(Person person, AdresseTo adresseTo, MappingContext context) {

                        if (!person.getBoadresse().isEmpty()) {
                            RsAdresse adresse = person.getBoadresse().get(0);
                            if (adresse instanceof RsGateadresse) {
                                adresseTo.setAdresse1(format("%s %s", ((RsGateadresse) adresse).getGateadresse(),
                                        ((RsGateadresse) adresse).getHusnummer()));

                            } else {
                                adresseTo.setAdresse1(((RsMatrikkeladresse) adresse).getMellomnavn());
                                adresseTo.setAdresse2(format("gårdsnummer: %s, bruksnr: %s",
                                        ((RsMatrikkeladresse) adresse).getGardsnr(),
                                        ((RsMatrikkeladresse) adresse).getBruksnr()));
                            }
                            adresseTo.setKommunenr(adresse.getKommunenr());
                            adresseTo.setPostnr(adresse.getPostnr());
                            adresseTo.setPoststed("UKJENT");
                            adresseTo.setLandKode("NO");

                        } else if (!person.getPostadresse().isEmpty()) {
                            RsPostadresse adresse = person.getPostadresse().get(0);
                            adresseTo.setAdresse1(adresse.getPostLinje1());
                            adresseTo.setAdresse2(adresse.getPostLinje2());
                            adresseTo.setAdresse3(adresse.getPostLinje3());
                            adresseTo.setLandKode(LandkoderIso3To2.getCountryIso2(adresse.getPostLand()));
                        }
                    }
                })
                .register();

        factory.classMap(Person.class, NavnTo.class)
                .customize(new CustomMapper<Person, NavnTo>() {
                    @Override
                    public void mapAtoB(Person person, NavnTo navnTo, MappingContext context) {

                        navnTo.setNavn1(person.getFornavn());
                        navnTo.setNavn2(person.getMellomnavn());
                        navnTo.setNavn3(person.getEtternavn());
                    }
                })
                .register();
    }
}