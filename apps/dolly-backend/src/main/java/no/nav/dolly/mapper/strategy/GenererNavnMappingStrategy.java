package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.PdlPerson.Navn;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class GenererNavnMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(JsonNode.class, Navn.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(JsonNode node, Navn navn, MappingContext context) {

                            navn.setFornavn(node.path("adjektiv").asString());
                            navn.setMellomnavn(node.path("adverb").asString());
                            navn.setEtternavn(node.path("substantiv").asString());
                    }
                })
                .byDefault()
                .register();

    }
}
