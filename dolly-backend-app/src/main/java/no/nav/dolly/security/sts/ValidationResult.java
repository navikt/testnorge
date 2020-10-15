package no.nav.dolly.security.sts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Value
@Builder
@AllArgsConstructor
public class ValidationResult {
    private String sub;
    private String iss;
    private LocalDateTime iat;
    private LocalDateTime exp;
    private String azp;
    private String aud;
    private String acr;
    private String oid;
    private Map<String, List<Object>> claims;

    public ValidationResult(Map<String, List<Object>> fromJose) {
        this.claims = fromJose;
        this.sub = (String) fromJose.get("sub").get(0);
        this.iss = (String) fromJose.get("iss").get(0);
        this.iat = LocalDateTime.ofInstant(Instant.ofEpochSecond((Long) fromJose.get("iat").get(0)), ZoneId.systemDefault());
        this.exp = LocalDateTime.ofInstant(Instant.ofEpochSecond((Long) fromJose.get("exp").get(0)), ZoneId.systemDefault());
        List<Object> azpList = fromJose.get("azp");
        this.azp = azpList == null ? "omitted" : (String) azpList.get(0);
        this.aud = (String) fromJose.get("aud").get(0);
        this.acr = fromJose.get("acr") == null ? null : (String) fromJose.get("acr").get(0);
        this.oid = fromJose.get("oid") == null ? null : (String) fromJose.get("oid").get(0);
    }
}