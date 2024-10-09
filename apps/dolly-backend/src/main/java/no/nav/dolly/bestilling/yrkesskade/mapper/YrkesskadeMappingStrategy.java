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

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

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
                                switch (destinasjon.getInnmelderrolle()) {
                                    case denSkadelidte -> ident;
                                    case vergeOgForesatt ->
                                            Optional.ofNullable(personBolk)
                                                    .flatMap(persondata -> persondata.getData().getHentPersonBolk().stream()
                                                            .map(PdlPersonBolk.PersonBolk::getPerson)
                                                            .map(PdlPerson.Person::getVergemaalEllerFremtidsfullmakt)
                                                            .flatMap(Collection::stream)
                                                            .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                                            .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                                                            .filter(Objects::nonNull)
                                                            .findFirst()).orElse(null);
                                    case virksomhetsrepresentant ->
                                            destinasjon.getSkadelidtIdentifikator();
                                });

                        if (destinasjon.getInnmelderrolle() == InnmelderRolletype.denSkadelidte ||
                                destinasjon.getInnmelderrolle() == InnmelderRolletype.vergeOgForesatt) {

                            destinasjon.setPaaVegneAv(destinasjon.getSkadelidtIdentifikator());
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
