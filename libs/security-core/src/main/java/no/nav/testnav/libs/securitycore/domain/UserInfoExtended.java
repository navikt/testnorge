package no.nav.testnav.libs.securitycore.domain;

import java.util.List;

public record UserInfoExtended (String id, String organisasjonsnummer, String issuer, String brukernavn, String epost, boolean isBankId, List<String> grupper) {
}
