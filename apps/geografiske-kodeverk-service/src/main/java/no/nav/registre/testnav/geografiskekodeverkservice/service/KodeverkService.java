package no.nav.registre.testnav.geografiskekodeverkservice.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.geografiskekodeverkservice.domain.Kodeverk;
import no.nav.registre.testnav.geografiskekodeverkservice.exceptions.DollyFunctionalException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

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

    public List<Kodeverk> getKommuner(String kommunenr, String kommunenavn) {
        return kommunerKodeverkListe
                .stream()
                .filter(kodeverk -> StringUtils.isBlank(kommunenr) || kommunenr.equals(kodeverk.getKode()))
                .filter(kodeverk -> StringUtils.isBlank(kommunenavn) || kommunenavn.equalsIgnoreCase(kodeverk.getNavn()))
                .toList();
    }

    public List<Kodeverk> getLand(String landkode, String land) {
        return landKodeverkListe
                .stream()
                .filter(kodeverk -> StringUtils.isBlank(landkode) || landkode.equals(kodeverk.getKode()))
                .filter(kodeverk -> StringUtils.isBlank(land) || land.equalsIgnoreCase(kodeverk.getNavn()))
                .toList();
    }

    public List<Kodeverk> getPostnummer(String postnummer, String poststed) {
        return postnummerKodeverkListe
                .stream()
                .filter(kodeverk -> StringUtils.isBlank(postnummer) || postnummer.equals(kodeverk.getKode()))
                .filter(kodeverk -> StringUtils.isBlank(poststed) || poststed.equalsIgnoreCase(kodeverk.getNavn()))
                .toList();
    }

    public List<Kodeverk> getEmbeter(String embetekode, String embetenavn) {
        return embeterKodeverkListe
                .stream()
                .filter(kodeverk -> StringUtils.isBlank(embetekode) || embetekode.equals(kodeverk.getKode()))
                .filter(kodeverk -> StringUtils.isBlank(embetenavn) || embetenavn.equals(kodeverk.getNavn()))
                .toList();
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
                    .toList();
        } catch (IOException e) {
            throw new DollyFunctionalException("Lesing av kodeverk fra " + path + " feilet", e);
        }
    }
}
