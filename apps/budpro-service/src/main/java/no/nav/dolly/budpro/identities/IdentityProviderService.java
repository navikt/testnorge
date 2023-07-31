package no.nav.dolly.budpro.identities;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IdentityProviderService {

    List<String> getIdentities(int limit);

}
