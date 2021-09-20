package no.nav.testnav.libs.reactivesecurity.manager;

import com.nimbusds.jwt.JWT;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@FunctionalInterface
interface GetJwtDecoder {
    ReactiveJwtDecoder from(JWT jwt);
}
