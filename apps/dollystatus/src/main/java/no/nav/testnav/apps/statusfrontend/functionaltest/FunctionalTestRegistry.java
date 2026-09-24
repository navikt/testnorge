package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestKey;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FunctionalTestRegistry {

    private final List<RegisteredFunctionalTest> registrations;
    private final List<RegisteredTechnicalStatus> technicalRegistrations;

    @Autowired
    public FunctionalTestRegistry(
            List<FunctionalTestDefinition<?, ?, ?>> definitions,
            List<TechnicalStatusDefinition> technicalDefinitions
    ) {
        var registeredKeys = new HashSet<FunctionalTestKey>();
        var registeredTests = new ArrayList<RegisteredFunctionalTest>();
        var registeredTechnicalStatuses = new ArrayList<RegisteredTechnicalStatus>();

        definitions.forEach(definition -> definition.descriptor().environments().forEach(environment -> {
            var registration = new RegisteredFunctionalTest(definition, environment);
            if (!registeredKeys.add(registration.key())) {
                throw new IllegalStateException("Flere funksjonstester er registrert med samme system og miljø.");
            }
            registeredTests.add(registration);
        }));
        technicalDefinitions.forEach(definition -> definition.descriptor().environments().forEach(environment -> {
            var registration = new RegisteredTechnicalStatus(definition, environment);
            if (!registeredKeys.add(registration.key())) {
                throw new IllegalStateException("Flere statuser er registrert med samme system og miljø.");
            }
            registeredTechnicalStatuses.add(registration);
        }));

        registrations = registeredTests.stream()
                .sorted(Comparator
                        .comparing((RegisteredFunctionalTest registration) ->
                                registration.key().systemId().value())
                        .thenComparing(registration -> registration.environment().name()))
                .toList();
        technicalRegistrations = registeredTechnicalStatuses.stream()
                .sorted(Comparator
                        .comparing((RegisteredTechnicalStatus registration) ->
                                registration.key().systemId().value())
                        .thenComparing(registration -> registration.environment().name()))
                .toList();
    }

    public FunctionalTestRegistry(List<FunctionalTestDefinition<?, ?, ?>> definitions) {
        this(definitions, List.of());
    }

    public List<RegisteredFunctionalTest> registrations() {
        return registrations;
    }

    public List<RegisteredFunctionalTest> registrationsFor(SystemId systemId) {
        return registrations.stream()
                .filter(registration -> registration.key().systemId().equals(systemId))
                .toList();
    }

    public List<RegisteredTechnicalStatus> technicalRegistrations() {
        return technicalRegistrations;
    }

    public List<RegisteredTechnicalStatus> technicalRegistrationsFor(SystemId systemId) {
        return technicalRegistrations.stream()
                .filter(registration -> registration.key().systemId().equals(systemId))
                .toList();
    }

    public record RegisteredFunctionalTest(
            FunctionalTestDefinition<?, ?, ?> definition,
            FunctionalTestEnvironment environment
    ) {

        public FunctionalTestKey key() {
            return new FunctionalTestKey(definition.descriptor().systemId(), environment);
        }
    }

    public record RegisteredTechnicalStatus(
            TechnicalStatusDefinition definition,
            FunctionalTestEnvironment environment
    ) {

        public FunctionalTestKey key() {
            return new FunctionalTestKey(definition.descriptor().systemId(), environment);
        }
    }
}
