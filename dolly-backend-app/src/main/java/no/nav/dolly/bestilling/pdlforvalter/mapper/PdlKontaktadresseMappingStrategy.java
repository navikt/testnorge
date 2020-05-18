package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.PostadresseIFrittFormat;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.UtenlandskAdresseIFrittFormat;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.VegadresseForPost;
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

                            if ("GATE".equals(person.getBoadresse().get(0).getAdressetype())) {
                                kontaktadresse.setVegadresseForPost(mapperFacade.map(
                                        person.getBoadresse().get(0), VegadresseForPost.class));
                            }

                        } else if (!person.getPostadresse().isEmpty() && person.getPostadresse().get(0).isNorsk()) {
                            kontaktadresse.setPostadresseIFrittFormat(mapperFacade.map(
                                    person.getPostadresse().get(0), PostadresseIFrittFormat.class));
                        }

                        if (!person.getPostadresse().isEmpty() && !person.getPostadresse().get(0).isNorsk()) {
                            kontaktadresse.setUtenlandskAdresseIFrittFormat(mapperFacade.map(
                                    person.getPostadresse().get(0), UtenlandskAdresseIFrittFormat.class));
                        }
                    }
                })
                .register();

        factory.classMap(RsGateadresse.class, VegadresseForPost.class)
                .customize(new CustomMapper<RsGateadresse, VegadresseForPost>() {
                    @Override
                    public void mapAtoB(RsGateadresse gateadresse, VegadresseForPost vegadresse, MappingContext context) {

                        vegadresse.setAdressekode(gateadresse.getGatekode());
                        vegadresse.setAdressenavn(gateadresse.getGateadresse());
                        vegadresse.setHusnummer(gateadresse.getHusnummer());
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
