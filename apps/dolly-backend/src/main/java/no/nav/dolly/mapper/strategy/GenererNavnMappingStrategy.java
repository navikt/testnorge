package no.nav.dolly.mapper.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.PdlPerson.Navn;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class GenererNavnMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(JsonNode.class, Navn.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(JsonNode node, Navn navn, MappingContext context) {

                        if (node.has("adjektiv")) {
                            navn.setFornavn(node.get("adjektiv").textValue());
                        }
                        if (node.has("adverb")) {
                            navn.setMellomnavn(node.get("adverb").textValue());
                        }
                        if (node.has("substantiv")) {
                            navn.setEtternavn(node.get("substantiv").textValue());
                        }
                    }
                })
                .byDefault()
                .register();

    }
}
