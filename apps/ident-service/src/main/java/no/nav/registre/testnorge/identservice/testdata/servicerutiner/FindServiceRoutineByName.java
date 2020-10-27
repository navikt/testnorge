package no.nav.registre.testnorge.identservice.testdata.servicerutiner;

import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindServiceRoutineByName implements Command {

    @Autowired
    private GetTpsServiceRutinerService getTpsServiceRutinerService;

    public Optional<TpsServiceRoutineDefinitionRequest> execute() {
        return getTpsServiceRutinerService.execute()
                .stream()
                .findFirst();
    }

}
