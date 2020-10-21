package no.nav.freg.fasit.utils;

import java.io.IOException;
import java.util.List;

import no.nav.identpool.fasit.FasitResource;
import no.nav.identpool.fasit.ResourceType;
import no.nav.identpool.fasit.Zone;

public interface FasitService {

    String downloadFromUrl(String url, String fileName) throws IOException;

    String downloadFromUrl(String url, String targetDirectory, String fileName) throws IOException;

    <T extends FasitResource> T find(String alias, ResourceType type, String env, String application, Zone zone, Class<T> mappedType);

    String findSecret(String ref);

    List<String> findEnvironmentNames(String environmentClass);
}