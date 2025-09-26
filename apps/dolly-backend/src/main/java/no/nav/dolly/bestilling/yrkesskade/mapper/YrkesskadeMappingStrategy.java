package no.nav.dolly.bestilling.yrkesskade.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.yrkesskade.v1.InnmelderRolletype;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.DateZoneUtil.CET;

@Component
public class YrkesskadeMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(YrkesskadeRequest.class, YrkesskadeRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(YrkesskadeRequest kilde, YrkesskadeRequest destinasjon, MappingContext context) {

                        var ident = (String) context.getProperty("ident");
                        var personBolk = (PdlPersonBolk) context.getProperty("personBolk");

                        destinasjon.setSkadelidtIdentifikator(ident);
                        destinasjon.setInnmelderIdentifikator(
                                nonNull(destinasjon.getInnmelderrolle()) ?
                                switch (destinasjon.getInnmelderrolle()) {
                                    case denSkadelidte -> ident;
                                    case vergeOgForesatt ->
                                            getVergePerson(personBolk);
                                    case virksomhetsrepresentant ->
                                            "15846297631";
                                } : null);

                        if (destinasjon.getInnmelderrolle() == InnmelderRolletype.denSkadelidte ||
                                destinasjon.getInnmelderrolle() == InnmelderRolletype.vergeOgForesatt) {

                            destinasjon.setPaaVegneAv(destinasjon.getSkadelidtIdentifikator());

                        } else {
                            destinasjon.setPaaVegneAv("315286255");
                        }
                    }
                })
                .byDefault()
                .register();
    }

    private String getVergePerson(PdlPersonBolk persondata) {

        if (isNull(persondata)) {
            return null;
        }

        var verge = persondata.getData().getHentPersonBolk().stream()
                .map(PdlPersonBolk.PersonBolk::getPerson)
                .map(PdlPerson.Person::getVergemaalEllerFremtidsfullmakt)
                .flatMap(Collection::stream)
                .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);

        if (isNull(verge) && persondata.getData().getHentPersonBolk().stream()
                .map(PdlPersonBolk.PersonBolk::getPerson)
                .map(PdlPerson.Person::getFoedselsdato)
                .flatMap(Collection::stream)
                .map(foedselsdato -> nonNull(foedselsdato.getFoedselsdato()) ?
                        ChronoUnit.YEARS.between(foedselsdato.getFoedselsdato(), LocalDate.now(CET)) :
                        LocalDate.now(CET).getYear() - foedselsdato.getFoedselsaar())
                .anyMatch(age -> age < 18)) {

            verge = persondata.getData().getHentPersonBolk().stream()
                    .map(PdlPersonBolk.PersonBolk::getPerson)
                    .map(PdlPerson.Person::getForelderBarnRelasjon)
                    .flatMap(Collection::stream)
                    .filter(PdlPerson.ForelderBarnRelasjon::isForelder)
                    .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                    .findFirst().orElse(null);
        }

        return verge;
    }
}
