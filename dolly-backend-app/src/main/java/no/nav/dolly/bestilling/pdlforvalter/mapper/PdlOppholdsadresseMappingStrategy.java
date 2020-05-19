package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse.Bruksenhetstype;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse.Matrikkeladresse;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse.UtenlandskAdresse;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse.Vegadresse;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse.OppholdAnnetSted;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsMatrikkeladresse;
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
                                    person.getBoadresse().get(0).getFlyttedato().toLocalDate());
                            if ("GATE".equals(person.getBoadresse().get(0).getAdressetype())) {
                                oppholdsadresse.setVegadresse(mapperFacade.map(
                                        person.getBoadresse().get(0), Vegadresse.class));
                            } else {
                                oppholdsadresse.setMatrikkeladresse(mapperFacade.map(
                                        person.getBoadresse().get(0), Matrikkeladresse.class));
                            }

                        } else if (!person.getPostadresse().isEmpty() &&
                                !person.getPostadresse().get(0).isNorsk()) {
                            oppholdsadresse.setUtenlandskAdresse(mapperFacade.map(
                                    person.getPostadresse().get(0), UtenlandskAdresse.class));
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

        factory.classMap(RsMatrikkeladresse.class, Matrikkeladresse.class)
                .customize(new CustomMapper<RsMatrikkeladresse, Matrikkeladresse>() {
                    @Override
                    public void mapAtoB(RsMatrikkeladresse rsMatrikkeladresse, Matrikkeladresse matrikkeladresse, MappingContext context) {

                        matrikkeladresse.setAdressetilleggsnavn(rsMatrikkeladresse.getMellomnavn());
                        matrikkeladresse.setBruksenhetstype(Bruksenhetstype.BOLIG);
                        matrikkeladresse.setBruksnummer(Integer.valueOf(rsMatrikkeladresse.getBruksnr()));
                        matrikkeladresse.setFestenummer(Integer.valueOf(rsMatrikkeladresse.getFestenr()));
                        matrikkeladresse.setGaardsnummer(Integer.valueOf(rsMatrikkeladresse.getGardsnr()));
                        matrikkeladresse.setKommunenummer(rsMatrikkeladresse.getKommunenr());
                        matrikkeladresse.setPostnummer(rsMatrikkeladresse.getPostnr());
                        matrikkeladresse.setUndernummer(Integer.valueOf(rsMatrikkeladresse.getUndernr()));
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
