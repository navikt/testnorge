package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static java.util.Objects.isNull;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.Bruksenhetstype;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.PostadresseIFrittFormat;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.UtenlandskAdresseIFrittFormat;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.Vegadresse;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlKontaktadresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlKontaktadresse.class)
                .customize(new CustomMapper<Person, PdlKontaktadresse>() {
                    @Override
                    public void mapAtoB(Person person, PdlKontaktadresse kontaktadresse, MappingContext context) {

                        kontaktadresse.setKilde(CONSUMER);

                        if (!person.getBoadresse().isEmpty()) {
                            kontaktadresse.setGyldigFraOgMed(
                                    person.getBoadresse().get(0).getFlyttedato().toLocalDate());
                            if (person.getBoadresse().get(0) instanceof RsGateadresse) {
                                kontaktadresse.setVegadresse(mapperFacade.map(
                                        person.getBoadresse().get(0), Vegadresse.class));
                            }

                        } else if (!person.getPostadresse().isEmpty()) {

                            if ("NOR".equals(person.getPostadresse().get(0).getPostLand()) ||
                                    isNull(person.getPostadresse().get(0).getPostLand())) {

                                kontaktadresse.setPostadresseIFrittFormat(mapperFacade.map(
                                        person.getPostadresse().get(0), PostadresseIFrittFormat.class));
                            } else {
                                kontaktadresse.setUtenlandskAdresseIFrittFormat(mapperFacade.map(
                                        person.getPostadresse().get(0), UtenlandskAdresseIFrittFormat.class));
                            }
                        }
                    }
                })
                .register();

        factory.classMap(RsGateadresse.class, Vegadresse.class)
                .customize(new CustomMapper<RsGateadresse, Vegadresse>() {
                    @Override
                    public void mapAtoB(RsGateadresse gateadresse, Vegadresse vegadresse, MappingContext context) {

                        vegadresse.setAdressekode(gateadresse.getGatekode());
                        vegadresse.setAdressenavn(gateadresse.getGateadresse());
                        vegadresse.setHusnummer(gateadresse.getHusnummer());
                        vegadresse.setBruksenhetstype(Bruksenhetstype.BOLIG);
                        vegadresse.setKommunenummer(gateadresse.getKommunenr());
                        vegadresse.setPostnummer(gateadresse.getPostnr());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsPostadresse.class, PostadresseIFrittFormat.class)
                .customize(new CustomMapper<RsPostadresse, PostadresseIFrittFormat>() {
                    @Override
                    public void mapAtoB(RsPostadresse postadresse, PostadresseIFrittFormat postadresseIFrittFormat, MappingContext context) {

                        postadresseIFrittFormat.getAdresselinjer().add(postadresse.getPostLinje1());
                        if (isNotBlank(postadresse.getPostLinje2())) {
                            postadresseIFrittFormat.getAdresselinjer().add(postadresse.getPostLinje2());
                        }
                        if (isNotBlank(postadresse.getPostLinje3())) {
                            postadresseIFrittFormat.getAdresselinjer().add(postadresse.getPostLinje3());
                        }
                    }
                })
                .register();

        factory.classMap(RsPostadresse.class, UtenlandskAdresseIFrittFormat.class)
                .customize(new CustomMapper<RsPostadresse, UtenlandskAdresseIFrittFormat>() {
                    @Override
                    public void mapAtoB(RsPostadresse postadresse, UtenlandskAdresseIFrittFormat utenlandskAdresse, MappingContext context) {

                        utenlandskAdresse.getAdresselinjer().add(postadresse.getPostLinje1());
                        if (isNotBlank(postadresse.getPostLinje2())) {
                            utenlandskAdresse.getAdresselinjer().add(postadresse.getPostLinje2());
                        }
                        if (isNotBlank(postadresse.getPostLinje3())) {
                            utenlandskAdresse.getAdresselinjer().add(postadresse.getPostLinje3());
                        }
                        utenlandskAdresse.setLandkode(postadresse.getPostLand());
                    }
                })
                .register();
    }
}
