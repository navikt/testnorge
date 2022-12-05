package no.nav.dolly.bestilling.brregstub.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.mapper.MappingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo.AdresseTo;
import static no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo.NavnTo;
import static no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo.RolleTo;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
public class BrregRolleMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsBregdata.class, RolleoversiktTo.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsBregdata bregdata, RolleoversiktTo rolleoversiktTo, MappingContext context) {

                        var personBolk = (PdlPersonBolk.PersonBolk) context.getProperty("personBolk");

                        rolleoversiktTo.setAdresse(mapperFacade.map(personBolk.getPerson(), AdresseTo.class));
                        rolleoversiktTo.setEnheter(mapperFacade.mapAsList(bregdata.getEnheter(), RolleTo.class));
                        rolleoversiktTo.setFnr(personBolk.getIdent());
                        rolleoversiktTo.setFodselsdato(personBolk.getPerson().getFoedsel().stream().findFirst()
                                .orElse(new PdlPerson.Foedsel())
                                .getFoedselsdato());
                        rolleoversiktTo.setHovedstatus(0);
                        rolleoversiktTo.setNavn(mapperFacade.map(personBolk.getPerson().getNavn().stream().findFirst()
                                .orElse(new PdlPerson.Navn()), NavnTo.class));
                        rolleoversiktTo.setUnderstatuser(bregdata.getUnderstatuser());
                    }
                })
                .register();

        factory.classMap(RsBregdata.RolleTo.class, RolleTo.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsBregdata.RolleTo rsRolleTo, RolleTo rolleTo, MappingContext context) {

                        rolleTo.setPersonRolle(mapperFacade.mapAsList(rsRolleTo.getPersonroller(), RolleoversiktTo.RolleStatus.class));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(PdlPerson.Person.class, AdresseTo.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlPerson.Person person, AdresseTo adresseTo, MappingContext context) {

                        if (!person.getBostedsadresse().isEmpty()) {
                            var adresse = person.getBostedsadresse().get(0);
                            if (nonNull(adresse.getVegadresse())) {
                                adresseTo.setAdresse1(format("%s %s", adresse.getVegadresse().getAdressenavn(),
                                        adresse.getVegadresse().getHusnummer()));
                                adresseTo.setKommunenr(adresse.getVegadresse().getKommunenummer());
                                adresseTo.setPostnr(adresse.getVegadresse().getPostnummer());
                                adresseTo.setPoststed("UKJENT");
                                adresseTo.setLandKode("NO");

                            } else if (nonNull(adresse.getUtenlandskAdresse())) {

                                adresseTo.setAdresse1(isNotBlank(adresse.getUtenlandskAdresse().getAdressenavnNummer()) ?
                                        adresse.getUtenlandskAdresse().getAdressenavnNummer() :
                                        adresse.getUtenlandskAdresse().getPostboksNummerNavn());
                                adresseTo.setAdresse2(Stream.of(adresse.getUtenlandskAdresse().getBySted(),
                                                adresse.getUtenlandskAdresse().getPostkode())
                                        .filter(StringUtils::isNotBlank)
                                        .collect(Collectors.joining(" ")));
                                adresseTo.setAdresse3(Stream.of(adresse.getUtenlandskAdresse().getRegion(),
                                                adresse.getUtenlandskAdresse().getRegionDistriktOmraade(),
                                                adresse.getUtenlandskAdresse().getDistriktsnavn())
                                        .filter(StringUtils::isNotBlank)
                                        .collect(Collectors.joining(" ")));
                                adresseTo.setLandKode(LandkoderIso3To2.getCountryIso2(adresse.getUtenlandskAdresse().getLandkode()));
                            } else {
                                adresseTo.setAdresse1("FYRSTIKKALLÉEN 2");
                                adresseTo.setLandKode("NO");
                            }
                        }
                    }
                })
                .register();

        factory.classMap(PdlPerson.Navn.class, NavnTo.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlPerson.Navn navn, NavnTo navnTo, MappingContext context) {

                        navnTo.setNavn1(navn.getFornavn());
                        navnTo.setNavn2(navn.getMellomnavn());
                        navnTo.setNavn3(navn.getEtternavn());
                    }
                })
                .register();
    }
}