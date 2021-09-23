package no.nav.pdl.forvalter.domain;

import java.util.function.Function;

import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;

@FunctionalInterface
public interface OrdersWithAccessToken extends Function<AccessToken, OrdreResponseDTO.PdlStatusDTO> {
}
