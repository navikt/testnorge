package no.nav.dolly.libs.nais.init;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.PrivateKey;

/**
 * Converts NAIS provided key.pem to PKCS#8 PEM format, which can be used by R2dbc.
 */
@Slf4j
public class PemToPkcs8Converter implements Runnable {

    private static final String INPUT_PATH = "/var/run/secrets/nais.io/sqlcertificate/key.pem";
    private static final String OUTPUT_PATH = "/tmp/pk8.pem"; // Note: Should match configuration in spring.r2dbc.properties.sslKey.

    public void run() {

        var inputFile = new File(INPUT_PATH);
        if (!inputFile.exists()) {
            log.warn("Input file {} does not exist; hopefully you're running locally", INPUT_PATH);
            return;
        }
        var outputFile = new File(OUTPUT_PATH);
        if (outputFile.exists()) {
            log.info("Output file {} already exists; skipping conversion", OUTPUT_PATH);
            return;
        }

        try (var pemParser = new PEMParser(new FileReader(inputFile))) {
            var object = pemParser.readObject();
            var converter = new JcaPEMKeyConverter();
            PrivateKey privateKey;
            if (object instanceof PEMKeyPair) {
                privateKey = converter.getPrivateKey(((PEMKeyPair) object).getPrivateKeyInfo());
            } else if (object instanceof PrivateKeyInfo) {
                privateKey = converter.getPrivateKey((PrivateKeyInfo) object);
            } else {
                throw new IllegalArgumentException("Unsupported PEM object: " + object.getClass());
            }
            try (var writer = new PemWriter(new FileWriter(outputFile))) {
                writer.writeObject(new PemObject("PRIVATE KEY", privateKey.getEncoded()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert PEM to PKCS#8", e);
        }
        log.info("Successfully converted {} to {}", INPUT_PATH, OUTPUT_PATH);

    }
}

