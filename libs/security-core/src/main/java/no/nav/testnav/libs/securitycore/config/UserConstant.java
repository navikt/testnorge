package no.nav.testnav.libs.securitycore.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConstant {

    public static final String USER_HEADER_JWT = "User-Jwt";
    public static final String USER_CLAIM_ID = "id";
    public static final String USER_CLAIM_USERNAME = "brukernavn";
    public static final String USER_CLAIM_ORG = "org";
    public static final String USER_CLAIM_EMAIL = "epost";
}