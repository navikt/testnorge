package no.nav.dolly.libs.texas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TexasConsumer {

    /**
     * <p>The unique name of this consumer within the current configuration.</p>
     * <p>Used by {@code Texas} to look up the consumer, but is not used in the token request.</p>
     */
    private String name;

    /**
     * <p>The audience of the token. This is the intended recipient of the token.</p>
     * <p>Written on the form {@code api://<cluster>.<namespace>.<other-api-app-name>/.default}.</p>
     */
    private String audience;

}
