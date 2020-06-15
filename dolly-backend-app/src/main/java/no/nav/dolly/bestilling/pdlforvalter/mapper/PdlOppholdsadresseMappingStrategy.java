package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse.UtenlandskAdresse;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse.Vegadresse;
import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getCoadresse;
import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getDato;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdresse.OppholdAnnetSted;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlMatrikkeladresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlOppholdsadresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlOppholdsadresse.class)
                .customize(new CustomMapper<Person, PdlOppholdsadresse>() {
                    @Override
                    public void mapAtoB(Person person, PdlOppholdsadresse oppholdsadresse, MappingContext context) {

                        oppholdsadresse.setKilde(CONSUMER);
                        oppholdsadresse.setOppholdAnnetSted(mapSpesReg(person.getSpesreg()));

                        if (!person.getBoadresse().isEmpty()) {
                            oppholdsadresse.setOppholdsadressedato(
                                    getDato(person.getBoadresse().get(0).getFlyttedato()));

                            if ("GATE".equals(person.getBoadresse().get(0).getAdressetype())) {
                                oppholdsadresse.setVegadresse(mapperFacade.map(
                                        person.getBoadresse().get(0), Vegadresse.class));
                                oppholdsadresse.setCoAdressenavn(getCoadresse(person.getBoadresse().get(0)));
                            } else {
                                oppholdsadresse.setMatrikkeladresse(mapperFacade.map(
                                        person.getBoadresse().get(0), PdlMatrikkeladresse.class));
                                oppholdsadresse.setCoAdressenavn(getCoadresse(person.getBoadresse().get(0)));
                            }
                        } else if (!person.getPostadresse().isEmpty() &&
                                !person.getPostadresse().get(0).isNorsk()) {
                            oppholdsadresse.setUtenlandskAdresse(mapperFacade.map(
                                    person.getPostadresse().get(0), UtenlandskAdresse.class));
                        }
                    }
                })
                .register();

        factory.classMap(RsPostadresse.class, UtenlandskAdresse.class)
                .customize(new CustomMapper<RsPostadresse, UtenlandskAdresse>() {
                    @Override
                    public void mapAtoB(RsPostadresse postadresse, UtenlandskAdresse utenlandskAdresse, MappingContext context) {

                        StringBuilder adresse = new StringBuilder(postadresse.getPostLinje1());
                        if (isNotBlank(postadresse.getPostLinje2())) {
                            adresse.append(' ')
                                    .append(postadresse.getPostLinje2());
                        }
                        if (isNotBlank(postadresse.getPostLinje3())) {
                            adresse.append(' ')
                                    .append(postadresse.getPostLinje3());
                        }
                        utenlandskAdresse.setAdressenavnNummer(adresse.toString());
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
