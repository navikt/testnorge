package no.nav.testnav.apps.brukerservice.service.v1;


import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class CryptographyService {
    private static final String ALGORITHM = "HmacSHA256";
    private final String secretKey;

    public CryptographyService(@Value("${CRYPTOGRAPHY_SECRET}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String createId(String userId, String representing) {
        return encode(userId + "." + representing);
    }

    @SneakyThrows
    public String encode(String data) {
        var hmacSHA256 = Mac.getInstance(ALGORITHM);
        var secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        hmacSHA256.init(secret_key);
        return Hex.toHexString(hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

}