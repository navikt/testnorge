package no.nav.registre.inntekt.domain;

import java.util.Set;

public class RsUser {

    private String navn;
    private String brukernavn;
    private Set<String> roller;
    private String token;

    public RsUser() {
    }

    public RsUser(String displayName, String username, Set<String> roles, String sessionId) {
        this.navn = displayName;
        this.brukernavn = username;
        this.roller = roles;
        this.token = sessionId;
    }

    public String getNavn() {
        return this.navn;
    }

    public String getBrukernavn() {
        return this.brukernavn;
    }

    public Set<String> getRoller() {
        return this.roller;
    }

    public String getToken() {
        return this.token;
    }
}
