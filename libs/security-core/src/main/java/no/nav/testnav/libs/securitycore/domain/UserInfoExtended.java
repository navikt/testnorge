package no.nav.testnav.libs.securitycore.domain;

public record UserInfoExtended (String id, String organisasjonsnummer, String issuer, String brukernavn, String epost, boolean isBankId) {
}
