package no.nav.dolly.budpro.identities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocalIdentityProviderService implements IdentityProviderService {

    @Override
    public synchronized List<String> getIdentities(int limit) {
        var actualLimit = Math.min(limit, 100000);
        var identities = new ArrayList<String>(actualLimit);
        for (int i = 0; i < actualLimit; i++) {
            identities.add("Z%05d".formatted(i));
        }
        return Collections.unmodifiableList(identities);
    }
}
