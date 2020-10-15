package no.nav.dolly.security.sts;

import java.util.Collections;
import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class OidcTokenAuthentication extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 0L;

    private final String principal;
    private final String issuer;
    private final String consumerToken;
    private final String idToken;
    private transient ValidationResult validationResult; // If your application is stateful and stores sessions in e.g. the database. Don't depend on this as it's transient!

    /**
     * Used to construct an instance that is to be sent to an AuthenticationManager.
     * Is therefore not authenticated (setAuthentication(false));
     */
    public OidcTokenAuthentication(String idToken, String consumerToken, ValidationResult validationResult) {
        super(Collections.<GrantedAuthority>emptyList());
        this.principal = null;
        this.issuer = null;
        this.consumerToken = consumerToken;
        this.validationResult = validationResult;
        this.idToken = idToken;
        super.setAuthenticated(false);
    }

    public OidcTokenAuthentication(String principal, String issuer, String idToken, String consumerToken, List<GrantedAuthority> grantedAuthorities) {
        super(grantedAuthorities);
        this.principal = principal;
        this.issuer = issuer;
        this.consumerToken = consumerToken;
        this.idToken = idToken;
        this.validationResult = null;
        super.setAuthenticated(true);
    }

    public String getIdTokenBody() {
        return idToken.split("\\.")[1]; // NOSONAR - Does not like that we split a string in a method
    }

    public String getConsumerTokenBody() {
        return consumerToken == null ? null : consumerToken.split("\\.")[1]; // NOSONAR - Does not like that we split a string in a method
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        // Set through constructor
    }
}