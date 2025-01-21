package no.nav.testnav.libs.securitycore.oauth2.client.userinfo;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import reactor.core.publisher.Mono;

class NoopReactiveClientRegistrationRepository implements ReactiveClientRegistrationRepository {

    @Override
    public Mono<ClientRegistration> findByRegistrationId(String registrationId) {
        return Mono.empty();
    }

}
