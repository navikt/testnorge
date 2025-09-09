package no.nav.testnav.levendearbeidsforholdansettelse;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemWriter;
import org.bouncycastle.util.io.pem.PemObject;

import java.io.*;
import java.security.PrivateKey;

@Slf4j
class PemToPkcs8Converter {

    static void convertIfNeeded(String inputPath, String outputPath) {

        var inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            log.warn("Input PEM file {} does not exist; hopefully you're running locally", inputPath);
            return;
        }
        var outputFile = new File(outputPath);
        if (outputFile.exists()) {
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

    }
}

