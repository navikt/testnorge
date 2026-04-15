package no.nav.pdl.forvalter.mapper;

import lombok.val;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Component
public class PersonMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(DbPerson.class, FullPersonDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(DbPerson kilde, FullPersonDTO destinasjon, MappingContext context) {

                        val relasjoner = (List<DbRelasjon>) context.getProperty("relasjoner");
                        val relatertePersoner = (Map<Long, DbPerson>) context.getProperty("relatertePersoner");

                        if (nonNull(relasjoner) && nonNull(relatertePersoner)) {
                            destinasjon.setRelasjoner(relasjoner.stream()
                                    .filter(relasjon -> relasjon.getPersonId().equals(kilde.getId()))
                                    .map(relasjon -> {
                                        val relatertPerson = relatertePersoner.get(relasjon.getRelatertPersonId());
                                        return FullPersonDTO.RelasjonDTO.builder()
                                                .id(relasjon.getId())
                                                .relasjonType(relasjon.getRelasjonType())
                                                .relatertPerson(relatertPerson.getPerson())
                                                .sistOppdatert(relasjon.getSistOppdatert())
                                                .build();
                                    })
                                    .toList());
                        }
                    }
                })
                .byDefault()
                .register();
    }
}