package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse.UtenlandskAdresse;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse.Vegadresse;
import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getCoadresse;
import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getDato;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdresse.OppholdAnnetSted;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlMatrikkeladresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresseHistorikk;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlOppholdsadresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlOppholdsadresseHistorikk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlOppholdsadresseHistorikk historikk, MappingContext context) {

                        person.getBoadresse().forEach(boAdresse -> {

                            PdlOppholdsadresse oppholdsadresse = new PdlOppholdsadresse();
                            oppholdsadresse.setGyldigFraOgMed(getDato(boAdresse.getFlyttedato()));
                            oppholdsadresse.setCoAdressenavn(getCoadresse(boAdresse));
                            if ("GATE".equals(boAdresse.getAdressetype())) {
                                oppholdsadresse.setVegadresse(mapperFacade.map(boAdresse, Vegadresse.class));
                            } else {
                                oppholdsadresse.setMatrikkeladresse(mapperFacade.map(
                                        boAdresse, PdlMatrikkeladresse.class));
                            }
                            oppholdsadresse.setKilde(CONSUMER);
                            oppholdsadresse.setOppholdAnnetSted(mapSpesReg(person.getSpesreg()));
                            historikk.getPdlAdresser().add(oppholdsadresse);
                        });

                        person.getPostadresse().forEach(postAdresse -> {
                            if (postAdresse.isUtenlandsk()) {

                                PdlOppholdsadresse oppholdsadresse = new PdlOppholdsadresse();
                                oppholdsadresse.setUtenlandskAdresse(mapperFacade.map(
                                        postAdresse, UtenlandskAdresse.class));

                                oppholdsadresse.setKilde(CONSUMER);
                                oppholdsadresse.setOppholdAnnetSted(mapSpesReg(person.getSpesreg()));
                                historikk.getPdlAdresser().add(oppholdsadresse);
                            }
                        });
                    }
                })
                .register();

        factory.classMap(RsPostadresse.class, UtenlandskAdresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPostadresse postadresse, UtenlandskAdresse utenlandskAdresse, MappingContext context) {

                        StringBuilder adresse = new StringBuilder();
                        if (isNotBlank(postadresse.getPostLinje1())) {
                            adresse.append(postadresse.getPostLinje1())
                                    .append(", ");
                        }
                        if (isNotBlank(postadresse.getPostLinje2())) {
                            adresse.append(postadresse.getPostLinje2())
                                    .append(", ");
                        }
                        if (isNotBlank(postadresse.getPostLinje3())) {
                            adresse.append(postadresse.getPostLinje3())
                                    .append(", ");
                        }
                        utenlandskAdresse.setAdressenavnNummer(adresse.substring(0, adresse.length() - 2));
                        utenlandskAdresse.setLandkode(postadresse.getPostLand());
                    }
                })
                .register();
    }

    private static OppholdAnnetSted mapSpesReg(String spesregCode) {

        if (isBlank(spesregCode)) {
            return null;
        }
        switch (spesregCode) {
        case "MILI":
            return OppholdAnnetSted.MILITAER;
        case "SVAL":
            return OppholdAnnetSted.PAA_SVALBARD;
        case "URIK":
            return OppholdAnnetSted.UTENRIKS;
        case "PEND":
            return OppholdAnnetSted.PENDLER;
        default:
            return null;
        }
    }
}
