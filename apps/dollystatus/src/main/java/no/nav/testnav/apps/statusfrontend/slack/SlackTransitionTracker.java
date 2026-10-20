package no.nav.testnav.apps.statusfrontend.slack;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestKey;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SlackTransitionTracker {

    private final Map<FunctionalTestKey, HealthState> states = new HashMap<>();

    public synchronized Optional<SlackTransition> update(FunctionalTestStatus status) {
        var currentState = healthState(status);
        if (currentState.isEmpty()) {
            return Optional.empty();
        }

        var key = new FunctionalTestKey(status.systemId(), status.environment());
        var previousState = states.put(key, currentState.get());
        if (currentState.get() == HealthState.RED && previousState != HealthState.RED) {
            return Optional.of(SlackTransition.RED);
        }
        return Optional.empty();
    }

    static boolean isSuccessful(FunctionalTestStatus status) {
        return status.state() == FunctionalTestState.OK
                || (status.state() == FunctionalTestState.TECHNICAL_ONLY
                && status.technicalStatus().state() == TechnicalStatusState.UP);
    }

    private Optional<HealthState> healthState(FunctionalTestStatus status) {
        if (isSuccessful(status)) {
            return Optional.of(HealthState.GREEN);
        }
        if (status.state() == FunctionalTestState.TECHNICAL_ONLY) {
            return status.technicalStatus().state() == TechnicalStatusState.DOWN
                    ? Optional.of(HealthState.RED)
                    : Optional.empty();
        }
        return switch (status.state()) {
            case PREFLIGHT_FAILED, CREATE_FAILED, VERIFY_FAILED, VERIFY_TIMEOUT, CLEANUP_FAILED ->
                    Optional.of(HealthState.RED);
            default -> Optional.empty();
        };
    }

    private enum HealthState {
        GREEN,
        RED
    }

    public enum SlackTransition {
        RED
    }
}
