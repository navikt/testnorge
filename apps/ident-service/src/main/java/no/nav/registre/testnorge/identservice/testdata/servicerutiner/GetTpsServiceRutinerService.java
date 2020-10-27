package no.nav.registre.testnorge.identservice.testdata.servicerutiner;

import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.resolvers.ServiceRoutineResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Service
public class GetTpsServiceRutinerService {

    @Autowired(required = false)
    private final List<ServiceRoutineResolver> resolvers = new ArrayList<>();

    public List<TpsServiceRoutineDefinitionRequest> execute() {
        return resolvers.stream()
                .map(ServiceRoutineResolver::resolve)
                .collect(toList());
    }
}