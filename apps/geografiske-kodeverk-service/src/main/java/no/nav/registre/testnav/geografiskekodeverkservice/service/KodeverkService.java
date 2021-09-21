package no.nav.registre.testnav.geografiskekodeverkservice.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.geografiskekodeverkservice.domain.Kodeverk;
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

    public List<Kodeverk> getKommuner(String kommunenr, String kommunenavn) {
        boolean toParametre = kommunenr != null && kommunenavn != null;
        return kommunerKodeverkListe
                .stream()
                .filter(kodeverk -> (kommunenr == null && kommunenavn == null) ||
                        (toParametre ? kodeverk.getKode().equals(kommunenr) && kodeverk.getNavn().equals(kommunenavn) :
                                kodeverk.getKode().equals(kommunenr) || kodeverk.getNavn().equals(kommunenavn)))
                .collect(Collectors.toList());
    }

    public List<Kodeverk> getLand(String landkode, String land) {
        boolean toParametre = landkode != null && land != null;
        return landKodeverkListe
                .stream()
                .filter(kodeverk -> (landkode == null && land == null) ||
                        (toParametre ? kodeverk.getKode().equals(landkode) && kodeverk.getNavn().equals(land) :
                                kodeverk.getKode().equals(landkode) || kodeverk.getNavn().equals(land)))
                .collect(Collectors.toList());
    }

    public List<Kodeverk> getPostnummer(String poststed, String postnummer) {
        boolean toParametre = poststed != null && postnummer != null;
        return postnummerKodeverkListe
                .stream()
                .filter(kodeverk -> (poststed == null && postnummer == null) ||
                        (toParametre ? kodeverk.getNavn().equals(poststed) && kodeverk.getKode().equals(postnummer) :
                                kodeverk.getNavn().equals(poststed) || kodeverk.getKode().equals(postnummer)))
                .collect(Collectors.toList());
    }

    public List<Kodeverk> getEmbeter() {
        return embeterKodeverkListe;
    }
}
