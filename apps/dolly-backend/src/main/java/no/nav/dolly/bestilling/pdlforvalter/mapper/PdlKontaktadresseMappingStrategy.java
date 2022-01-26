package no.nav.dolly.bestilling.pdlforvalter.mapper;

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
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigGateAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigPboxAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.MidlertidigAdresse.MidlertidigUtadAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.PERSON;
import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getDato;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret.UTVANDRET;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component
@RequiredArgsConstructor
public class PdlKontaktadresseMappingStrategy implements MappingStrategy {

    private static final String CO_NAME = "C/O";

    private static String getCoAdresse(MidlertidigAdresse midlertidigAdresse) {

        return isNotBlank(midlertidigAdresse.getTilleggsadresse()) &&
                midlertidigAdresse.getTilleggsadresse().contains(CO_NAME) ?
                midlertidigAdresse.getTilleggsadresse() : null;
    }

    private static void setUtgaatt(PdlKontaktadresse kontaktadresse, MappingContext mappingContext) {

        var innvandretUtvandret = ((Person) mappingContext.getProperty(PERSON)).getInnvandretUtvandret().stream()
                .findFirst().orElse(new InnvandretUtvandret());
        kontaktadresse.setFolkeregistermetadata(UTVANDRET == innvandretUtvandret.getInnutvandret() && "XUK".equals(innvandretUtvandret.getLandkode()) ?
                PdlOpplysning.Folkeregistermetadata.builder()
                        .gjeldende(false)
                        .build() : null);
    }

    private static PdlKontaktadresse buildKontaktadresse(MidlertidigAdresse midlertidigAdresse, MappingContext context) {

        PdlKontaktadresse kontaktadresse = new PdlKontaktadresse();
        kontaktadresse.setCoAdressenavn(getCoAdresse(midlertidigAdresse));
        kontaktadresse.setKilde(CONSUMER);
        kontaktadresse.setMaster(Master.FREG);
        kontaktadresse.setGyldigFraOgMed(LocalDate.now());
        kontaktadresse.setGyldigTilOgMed(getDato(midlertidigAdresse.getGyldigTom()));
        if (midlertidigAdresse.isGateAdr()) {
            kontaktadresse.setAdresseIdentifikatorFraMatrikkelen(
                    ((MidlertidigGateAdresse) midlertidigAdresse).getMatrikkelId());
        }

        setUtgaatt(kontaktadresse, context);
        return kontaktadresse;
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlKontaktadresseHistorikk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlKontaktadresseHistorikk historikk, MappingContext context) {

                        List<MidlertidigAdresse> midlertidigAdresser = new ArrayList<>(person.getMidlertidigAdresse());
                        Collections.reverse(midlertidigAdresser);

                        List<RsPostadresse> postadresser = new ArrayList<>(person.getPostadresse());
                        Collections.reverse(postadresser);

                        historikk.getPdlAdresser().addAll(
                                Stream.of(
                                                postadresser.stream()
                                                        .filter(RsPostadresse::isNorsk)
                                                        .filter(RsPostadresse::isValid)
                                                        .map(postadresse -> {
                                                            PdlKontaktadresse kontaktadresse = new PdlKontaktadresse();
                                                            kontaktadresse.setPostadresseIFrittFormat(mapperFacade.map(
                                                                    postadresse, PostadresseIFrittFormat.class));
                                                            kontaktadresse.setKilde(CONSUMER);
                                                            kontaktadresse.setMaster(Master.FREG);
                                                            setUtgaatt(kontaktadresse, context);
                                                            return kontaktadresse;
                                                        })
                                                        .collect(Collectors.toList()),

                                                postadresser.stream()
                                                        .filter(RsPostadresse::isUtenlandsk)
                                                        .filter(adr -> !person.isKode6())
                                                        .filter(RsPostadresse::isValid)
                                                        .map(postadresse -> {
                                                            PdlKontaktadresse kontaktadresse = new PdlKontaktadresse();
                                                            kontaktadresse.setUtenlandskAdresseIFrittFormat(mapperFacade.map(
                                                                    postadresse, UtenlandskAdresseIFrittFormat.class));
                                                            kontaktadresse.setKilde(CONSUMER);
                                                            kontaktadresse.setMaster(Master.PDL);
                                                            kontaktadresse.setGyldigFraOgMed(LocalDate.now().minusYears(1));
                                                            kontaktadresse.setGyldigTilOgMed(LocalDate.now().plusYears(10));
                                                            setUtgaatt(kontaktadresse, context);
                                                            return kontaktadresse;
                                                        })
                                                        .collect(Collectors.toList()),

                                                midlertidigAdresser.stream()
                                                        .filter(MidlertidigAdresse::isGateAdr)
                                                        .filter(adr -> !person.isKode6())
                                                        .map(midlertidigAdresse -> {
                                                            PdlKontaktadresse kontaktadresse = buildKontaktadresse(midlertidigAdresse, context);
                                                            kontaktadresse.setVegadresseForPost(
                                                                    mapperFacade.map(midlertidigAdresse, VegadresseForPost.class));
                                                            return kontaktadresse;
                                                        })
                                                        .collect(Collectors.toList()),

                                                midlertidigAdresser.stream()
                                                        .filter(MidlertidigAdresse::isPostBox)
                                                        .filter(adr -> !person.isKode6())
                                                        .map(midlertidigAdresse -> {
                                                            PdlKontaktadresse kontaktadresse = buildKontaktadresse(midlertidigAdresse, context);
                                                            kontaktadresse.setPostboksadresse(
                                                                    mapperFacade.map(midlertidigAdresse, Postboksadresse.class));
                                                            return kontaktadresse;
                                                        })
                                                        .collect(Collectors.toList()),

                                                midlertidigAdresser.stream()
                                                        .filter(MidlertidigAdresse::isUtenlandsk)
                                                        .filter(adr -> !person.isKode6())
                                                        .map(midlertidigAdresse -> {
                                                            PdlKontaktadresse kontaktadresse = buildKontaktadresse(midlertidigAdresse, context);
                                                            kontaktadresse.setUtenlandskAdresseIFrittFormat(
                                                                    mapperFacade.map(midlertidigAdresse, UtenlandskAdresseIFrittFormat.class));
                                                            return kontaktadresse;
                                                        })
                                                        .collect(Collectors.toList())

                                        )
                                        .flatMap(Collection::stream)
                                        .collect(Collectors.toList())
                        );
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
}
