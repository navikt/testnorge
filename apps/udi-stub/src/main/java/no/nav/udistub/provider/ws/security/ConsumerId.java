package no.nav.udistub.provider.ws.security;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import java.security.Principal;

public class ConsumerId implements Principal, Destroyable {

    private String consumerIdString;

    private boolean destroyed;

    public ConsumerId(String consumerId) {
        this.consumerIdString = consumerId;
    }

    @Override
    public void destroy() throws DestroyFailedException {
        consumerIdString = null;
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public String getName() {
        return consumerIdString;
    }

    public String getConsumerId() {
        return consumerIdString;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + (destroyed ? "destroyed" : consumerIdString) + "]";
    }
}
