package no.nav.registre.syntrest.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

@Component
public class NaisYaml {
    /**
     * Functionality for writing to NAIS-Yaml files
     */

    private final String manifestPath;

    public NaisYaml(@Value("${nais-manifest-path}") String manifestPath) {
        this.manifestPath = manifestPath;
    }

    public Map<String, Object> provideYaml(String appName, String tag) {
        var yaml = new Yaml();
        Map<String, Object> manifestFile = yaml.load(this.getClass().getResourceAsStream(manifestPath.replace("{appName}", appName)));

        Map<String, Object> spec = (Map) manifestFile.get("spec");
        var imageBase = spec.get("image").toString();
        String latestImage = imageBase.replace("latest", tag);
        spec.put("image", latestImage);

        return manifestFile;
    }
}
