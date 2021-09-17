package no.nav.registre.testnav.geografiskekodeverkservice.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.geografiskekodeverkservice.domain.Kodeverk;
import no.nav.registre.testnav.geografiskekodeverkservice.utils.ArtifactUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@NoArgsConstructor
public class KodeverkService {

    private static final List<Kodeverk> kommunerKodeverkListe;
    private static final List<Kodeverk> landKodeverkListe;
    private static final List<Kodeverk> postnummerKodeverkListe;
    private static final List<Kodeverk> embeterKodeverkListe;

    static {
        kommunerKodeverkListe = loadKodeverk("kommuner/kommuner.yaml");
        landKodeverkListe = loadKodeverk("landkoder/landkoder.yaml");
        postnummerKodeverkListe = loadKodeverk("postnummer/postnummer.yaml");
        embeterKodeverkListe = loadKodeverk("vergemaal/embeter.yaml");
    }

    private static List<Kodeverk> loadKodeverk(String path) {
        var resource = new ClassPathResource(path);
        try (final InputStreamReader stream = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            final Properties properties = new Properties();
            properties.load(stream);
            return properties
                    .entrySet()
                    .stream()
                    .map(Kodeverk::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Lesing av kodeverk fra " + path + " feilet", e);
        }
    }

    public List<Kodeverk> getKommuner(String kommunenr) {
        ArtifactUtils.isKommunenummer(kommunenr);
        return kommunerKodeverkListe
                .stream()
                .filter(kodeverk -> kommunenr == null || kodeverk.getKode().equals(kommunenr))
                .collect(Collectors.toList());
    }

    public static List<Kodeverk> getLand(String landkode) {
        ArtifactUtils.isLandkode(landkode);
        return landKodeverkListe
                .stream()
                .filter(kodeverk -> landkode == null || kodeverk.getKode().equals(landkode))
                .collect(Collectors.toList());
    }

    public static List<Kodeverk> getPostnummer(String poststed) {
        ArtifactUtils.isPoststed(poststed);
        return postnummerKodeverkListe
                .stream()
                .filter(kodeverk -> poststed == null || kodeverk.getNavn().equals(poststed))
                .collect(Collectors.toList());
    }

    public static List<Kodeverk> getEmbeter() {
        return embeterKodeverkListe;
    }
}
