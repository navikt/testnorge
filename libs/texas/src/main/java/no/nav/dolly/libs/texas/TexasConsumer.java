package no.nav.dolly.libs.texas;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>A configured Texas consumer, e.g. a service that uses Texas to obtain an access token.</p>
 * <p>Configured as one or more {@code dolly.consumers.*}.</p>
 */
@Getter
@Setter
public class TexasConsumer {

    /**
     * <p>The unique name of this consumer within the current configuration.</p>
     * <p>Used by {@code Texas} to look up the consumer, but is not used in the token request.</p>
     */
    private String name;

    /**
     * <p>The URL to the service.</p>
     * <p>Not used by {@code Texas}, but may be used by clients to create a {@code WebClient} through {@link Texas#webClient(String)}.</p>
     */
    private String url;

    /**
     * <p>The audience of the token. This is the intended recipient of the token.</p>
     * <p>Written on the form {@code api://<cluster>.<namespace>.<other-api-app-name>/.default}.</p>
     */
    private String audience;

}
