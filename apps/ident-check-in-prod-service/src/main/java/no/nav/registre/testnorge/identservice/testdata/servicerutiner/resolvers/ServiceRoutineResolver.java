package no.nav.registre.testnorge.identservice.testdata.servicerutiner.resolvers;


import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionRequest;

@FunctionalInterface
public interface ServiceRoutineResolver {
    TpsServiceRoutineDefinitionRequest resolve();
}