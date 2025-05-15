package no.nav.dolly.libs.texas;

/**
 * Represents a token received from the Texas service.
 *
 * @param access_token The access token.
 * @param expires_in   The expiration time of the token in seconds.
 * @param token_type   The type of the token (e.g. "Bearer").
 */
public record TexasToken(
        String access_token,
        String expires_in,
        String token_type) {
}