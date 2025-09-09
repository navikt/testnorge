package no.nav.dolly.libs.nais;

/**
 * Emulates legacy /init-scripts, intended to be run before starting the Spring context.
 */
public class InitScripts {

    public static void run(Runnable... initializers)
        throws IllegalStateException {
        for (var initializer : initializers) {
            try {
                initializer.run();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to run initializer %s".formatted(initializer.getClass().getSimpleName()), e);
            }
        }
    }

}
