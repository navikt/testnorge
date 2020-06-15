package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlPersonAdresseWrapper.Adressetype.NORSK;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlPersonAdresseWrapper.Adressetype.UTENLANDSK;
import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getDato;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigAdressetype.GATE;
import static no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigAdressetype.PBOX;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import java.util.List;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdresse.Adressegradering;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.PostadresseIFrittFormat;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.Postboksadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.UtenlandskAdresseIFrittFormat;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.VegadresseForPost;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlPersonAdresseWrapper;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigGateAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigPboxAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigUtadAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlKontaktadresseMappingStrategy implements MappingStrategy {

    private static final String CO_NAME = "C/O";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PdlPersonAdresseWrapper.class, PdlKontaktadresse.class)
                .customize(new CustomMapper<PdlPersonAdresseWrapper, PdlKontaktadresse>() {
                    @Override
                    public void mapAtoB(PdlPersonAdresseWrapper wrapper, PdlKontaktadresse kontaktadresse, MappingContext context) {

                        if (NORSK == wrapper.getAdressetype()) {

                            kontaktadresse.setCoAdressenavn(getCoAdresse(wrapper.getPerson()));
                            if (!wrapper.getPerson().getMidlertidigAdresse().isEmpty() &&
                                    GATE == wrapper.getPerson().getMidlertidigAdresse().get(0).getAdressetype()) {

                                kontaktadresse.setGyldigTilOgMed(
                                        wrapper.getPerson().getMidlertidigAdresse().get(0).getGyldigTom().toLocalDate());
                                kontaktadresse.setVegadresseForPost(
                                        mapperFacade.map(wrapper.getPerson().getMidlertidigAdresse().get(0), VegadresseForPost.class));

                            } else if (!wrapper.getPerson().getMidlertidigAdresse().isEmpty() &&
                                    PBOX == wrapper.getPerson().getMidlertidigAdresse().get(0).getAdressetype()) {

                                kontaktadresse.setGyldigTilOgMed(
                                        wrapper.getPerson().getMidlertidigAdresse().get(0).getGyldigTom().toLocalDate());
                                kontaktadresse.setPostboksadresse(mapperFacade.map(
                                        wrapper.getPerson().getMidlertidigAdresse().get(0), Postboksadresse.class));

                            } else if (!wrapper.getPerson().getBoadresse().isEmpty() &&
                                    !wrapper.getPerson().isUtenFastBopel() &&
                                    "GATE".equals(wrapper.getPerson().getBoadresse().get(0).getAdressetype())) {

                                kontaktadresse.setGyldigFraOgMed(
                                        getDato(wrapper.getPerson().getBoadresse().get(0).getFlyttedato()));

                                kontaktadresse.setVegadresseForPost(mapperFacade.map(
                                        wrapper.getPerson().getBoadresse().get(0), VegadresseForPost.class));

                            } else if (!wrapper.getPerson().getPostadresse().isEmpty()) {

                                kontaktadresse.setPostadresseIFrittFormat(mapperFacade.map(
                                        wrapper.getPerson().getPostadresse().get(0), PostadresseIFrittFormat.class));
                            }

                        } else if (UTENLANDSK == wrapper.getAdressetype()) {

                            if (!wrapper.getPerson().getMidlertidigAdresse().isEmpty() &&
                                    wrapper.getPerson().getMidlertidigAdresse().get(0).isUtenlandsk()) {

                                kontaktadresse.setUtenlandskAdresseIFrittFormat(mapperFacade.map(
                                        wrapper.getPerson().getMidlertidigAdresse().get(0), UtenlandskAdresseIFrittFormat.class));

                            } else if (!wrapper.getPerson().getPostadresse().isEmpty() &&
                               wrapper.getPerson().getPostadresse().get(0).isUtenlandsk()) {

                                kontaktadresse.setUtenlandskAdresseIFrittFormat(mapperFacade.map(
                                        wrapper.getPerson().getPostadresse().get(0), UtenlandskAdresseIFrittFormat.class));
                            }
                        }

                        kontaktadresse.setAdressegradering(Adressegradering.UGRADERT);
                        kontaktadresse.setKilde(CONSUMER);
                    }
                })
                .register();

        factory.classMap(MidlertidigGateAdresse.class, VegadresseForPost.class)
                .customize(new CustomMapper<MidlertidigGateAdresse, VegadresseForPost>() {
                    @Override
                    public void mapAtoB(MidlertidigGateAdresse gateadresse, VegadresseForPost vegadresse, MappingContext context) {

                        vegadresse.setAdressekode(gateadresse.getGatekode());
                        vegadresse.setAdressenavn(gateadresse.getGatenavn());
                        vegadresse.setHusnummer(gateadresse.getHusnr());
                        vegadresse.setPostnummer(gateadresse.getPostnr());
                        vegadresse.setAdressetillegsnavn(isNotBlank(gateadresse.getTilleggsadresse()) &&
                                !gateadresse.getTilleggsadresse().contains(CO_NAME) ?
                                gateadresse.getTilleggsadresse() : null);
                    }
                })
                .register();

        factory.classMap(BoGateadresse.class, VegadresseForPost.class)
                .customize(new CustomMapper<BoGateadresse, VegadresseForPost>() {
                    @Override
                    public void mapAtoB(BoGateadresse gateadresse, VegadresseForPost vegadresse, MappingContext context) {

                        vegadresse.setAdressekode(gateadresse.getGatekode());
                        vegadresse.setAdressenavn(gateadresse.getGateadresse());
                        vegadresse.setHusnummer(gateadresse.getHusnummer());
                        vegadresse.setPostnummer(gateadresse.getPostnr());
                        vegadresse.setAdressetillegsnavn(isNotBlank(gateadresse.getTilleggsadresse()) &&
                                !gateadresse.getTilleggsadresse().contains(CO_NAME) ?
                                gateadresse.getTilleggsadresse() : null);
                    }
                })
                .register();

        factory.classMap(MidlertidigPboxAdresse.class, Postboksadresse.class)
                .customize(new CustomMapper<MidlertidigPboxAdresse, Postboksadresse>() {
                    @Override
                    public void mapAtoB(MidlertidigPboxAdresse midlertidigAdresse, Postboksadresse postboksadresse, MappingContext context) {

                        postboksadresse.setPostboks(midlertidigAdresse.getPostboksnr());
                        postboksadresse.setPostbokseier(midlertidigAdresse.getPostboksAnlegg());
                        postboksadresse.setPostnummer(midlertidigAdresse.getPostnr());
                    }
                })
                .register();

        factory.classMap(RsPostadresse.class, PostadresseIFrittFormat.class)
                .customize(new CustomMapper<RsPostadresse, PostadresseIFrittFormat>() {
                    @Override
                    public void mapAtoB(RsPostadresse postadresse, PostadresseIFrittFormat postadresseIFrittFormat, MappingContext context) {

                        List<String> adresselinjer = newArrayList(postadresse.getPostLinje1());
                        if (isNotBlank(postadresse.getPostLinje2())) {
                            adresselinjer.add(postadresse.getPostLinje2());
                        }
                        if (isNotBlank(postadresse.getPostLinje3())) {
                            adresselinjer.add(postadresse.getPostLinje3());
                        }
                        postadresseIFrittFormat.setPostnummer(
                                adresselinjer.stream().reduce((first, second) -> second).get().split(" ")[0]);
                        adresselinjer.remove(adresselinjer.size() - 1);
                        postadresseIFrittFormat.setAdresselinjer(adresselinjer);
                    }
                })
                .register();

        factory.classMap(MidlertidigUtadAdresse.class, UtenlandskAdresseIFrittFormat.class)
                .customize(new CustomMapper<MidlertidigUtadAdresse, UtenlandskAdresseIFrittFormat>() {
                    @Override
                    public void mapAtoB(MidlertidigUtadAdresse utadAdresse, UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat, MappingContext context) {

                        utenlandskAdresseIFrittFormat.getAdresselinjer().add(utadAdresse.getPostLinje1());
                        if (isNotBlank(utadAdresse.getPostLinje2())) {
                            utenlandskAdresseIFrittFormat.getAdresselinjer().add(utadAdresse.getPostLinje2());
                        }
                        if (isNotBlank(utadAdresse.getPostLinje3())) {
                            utenlandskAdresseIFrittFormat.getAdresselinjer().add(utadAdresse.getPostLinje3());
                        }
                        utenlandskAdresseIFrittFormat.setLandkode(utadAdresse.getPostLand());
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

    private static String getCoAdresse(Person person) {

        if (!person.getMidlertidigAdresse().isEmpty()) {
            return isNotBlank(person.getMidlertidigAdresse().get(0).getTilleggsadresse()) &&
                    person.getMidlertidigAdresse().get(0).getTilleggsadresse().contains(CO_NAME) ?
                    person.getMidlertidigAdresse().get(0).getTilleggsadresse() : null;

        } else if (!person.getBoadresse().isEmpty()) {
            return isNotBlank(person.getBoadresse().get(0).getTilleggsadresse()) &&
                    person.getBoadresse().get(0).getTilleggsadresse().contains(CO_NAME) ?
                    person.getBoadresse().get(0).getTilleggsadresse() : null;

        } else {
            return null;
        }
    }
}
