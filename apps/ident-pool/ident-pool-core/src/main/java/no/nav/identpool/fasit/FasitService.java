package no.nav.identpool.fasit;

import java.io.IOException;
import java.util.List;

public interface FasitService {

    String downloadFromUrl(String url, String fileName) throws IOException;

    String downloadFromUrl(String url, String targetDirectory, String fileName) throws IOException;

    <T extends FasitResource> T find(String alias, ResourceType type, String env, String application, Zone zone, Class<T> mappedType);

    String findSecret(String ref);

    List<String> findEnvironmentNames(String environmentClass);
}