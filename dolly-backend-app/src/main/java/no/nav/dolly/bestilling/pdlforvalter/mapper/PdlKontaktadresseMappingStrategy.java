package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getDato;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigAdressetype.GATE;
import static no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigAdressetype.PBOX;
import static no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigAdressetype.STED;
import static no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigAdressetype.UTAD;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.PostadresseIFrittFormat;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.Postboksadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.UtenlandskAdresseIFrittFormat;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.VegadresseForPost;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresseHistorikk;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigGateAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigPboxAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigUtadAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;

@Component
@RequiredArgsConstructor
public class PdlKontaktadresseMappingStrategy implements MappingStrategy {

    private static final String CO_NAME = "C/O";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlKontaktadresseHistorikk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlKontaktadresseHistorikk historikk, MappingContext context) {

                        person.getBoadresse().forEach(boAdresse -> {
                            if (!person.isUtenFastBopel() && "GATE".equals(boAdresse.getAdressetype())) {

                                PdlKontaktadresse kontaktadresse = new PdlKontaktadresse();
                                kontaktadresse.setGyldigFraOgMed(
                                        getDato(boAdresse.getFlyttedato()));
                                kontaktadresse.setVegadresseForPost(mapperFacade.map(
                                        boAdresse, VegadresseForPost.class));
                                kontaktadresse.setCoAdressenavn(getCoAdresse(boAdresse));
                                kontaktadresse.setKilde(CONSUMER);
                                historikk.getPdlAdresser().add(kontaktadresse);
                            }
                        });
                        person.getMidlertidigAdresse().forEach(midlertidigAdresse -> {

                            if (STED != midlertidigAdresse.getAdressetype()) {
                                PdlKontaktadresse kontaktadresse = new PdlKontaktadresse();
                                kontaktadresse.setGyldigTilOgMed(getDato(midlertidigAdresse.getGyldigTom()));

                                if (GATE == midlertidigAdresse.getAdressetype()) {

                                    kontaktadresse.setVegadresseForPost(
                                            mapperFacade.map(midlertidigAdresse, VegadresseForPost.class));

                                } else if (PBOX == midlertidigAdresse.getAdressetype()) {

                                    kontaktadresse.setPostboksadresse(
                                            mapperFacade.map(midlertidigAdresse, Postboksadresse.class));

                                } else if (UTAD == midlertidigAdresse.getAdressetype()) {

                                    kontaktadresse.setUtenlandskAdresseIFrittFormat(
                                            mapperFacade.map(midlertidigAdresse, UtenlandskAdresseIFrittFormat.class));
                                }
                                kontaktadresse.setCoAdressenavn(getCoAdresse(midlertidigAdresse));
                                kontaktadresse.setKilde(CONSUMER);
                                historikk.getPdlAdresser().add(kontaktadresse);
                            }
                        });

                        person.getPostadresse().forEach(postAdresse -> {
                            if (isNotBlank(postAdresse.getPostLinje1())) {
                                PdlKontaktadresse kontaktadresse = new PdlKontaktadresse();
                                if (postAdresse.isNorsk()) {
                                    kontaktadresse.setPostadresseIFrittFormat(mapperFacade.map(
                                            postAdresse, PostadresseIFrittFormat.class));
                                } else {
                                    kontaktadresse.setUtenlandskAdresseIFrittFormat(mapperFacade.map(
                                            postAdresse, UtenlandskAdresseIFrittFormat.class));
                                }
                                kontaktadresse.setKilde(CONSUMER);
                                historikk.getPdlAdresser().add(kontaktadresse);
                            }
                        });
                    }
                }).register();

        factory.classMap(MidlertidigGateAdresse.class, VegadresseForPost.class)
                .customize(new CustomMapper<>() {
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
                }).register();

        factory.classMap(MidlertidigPboxAdresse.class, Postboksadresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(MidlertidigPboxAdresse midlertidigAdresse, Postboksadresse postboksadresse, MappingContext context) {

                        postboksadresse.setPostboks(midlertidigAdresse.getPostboksnr());
                        postboksadresse.setPostbokseier(midlertidigAdresse.getPostboksAnlegg());
                        postboksadresse.setPostnummer(midlertidigAdresse.getPostnr());
                    }
                }).register();

        factory.classMap(MidlertidigUtadAdresse.class, UtenlandskAdresseIFrittFormat.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(MidlertidigUtadAdresse utadAdresse, UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat, MappingContext context) {

                        if (isNotBlank(utadAdresse.getPostLinje1())) {
                            utenlandskAdresseIFrittFormat.getAdresselinjer().add(utadAdresse.getPostLinje1());
                        }
                        if (isNotBlank(utadAdresse.getPostLinje2())) {
                            utenlandskAdresseIFrittFormat.getAdresselinjer().add(utadAdresse.getPostLinje2());
                        }
                        if (isNotBlank(utadAdresse.getPostLinje3())) {
                            utenlandskAdresseIFrittFormat.getAdresselinjer().add(utadAdresse.getPostLinje3());
                        }
                        utenlandskAdresseIFrittFormat.setLandkode(utadAdresse.getPostLand());
                    }
                }).register();

        factory.classMap(RsPostadresse.class, UtenlandskAdresseIFrittFormat.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPostadresse postadresse, UtenlandskAdresseIFrittFormat utenlandskAdresse, MappingContext context) {

                        if (isNotBlank(postadresse.getPostLinje1())) {
                            utenlandskAdresse.getAdresselinjer().add(postadresse.getPostLinje1());
                        }
                        if (isNotBlank(postadresse.getPostLinje2())) {
                            utenlandskAdresse.getAdresselinjer().add(postadresse.getPostLinje2());
                        }
                        if (isNotBlank(postadresse.getPostLinje3())) {
                            utenlandskAdresse.getAdresselinjer().add(postadresse.getPostLinje3());
                        }
                        utenlandskAdresse.setLandkode(postadresse.getPostLand());
                    }
                }).register();
    }

    private static String getCoAdresse(MidlertidigAdresse midlertidigAdresse) {

        return isNotBlank(midlertidigAdresse.getTilleggsadresse()) &&
                midlertidigAdresse.getTilleggsadresse().contains(CO_NAME) ?
                midlertidigAdresse.getTilleggsadresse() : null;
    }

    private static String getCoAdresse(BoAdresse boAdresse) {

        return isNotBlank(boAdresse.getTilleggsadresse()) &&
                boAdresse.getTilleggsadresse().contains(CO_NAME) ?
                boAdresse.getTilleggsadresse() : null;
    }
}
