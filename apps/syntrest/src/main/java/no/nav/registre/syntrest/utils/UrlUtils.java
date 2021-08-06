package no.nav.registre.syntrest.utils;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilderFactory;

import java.net.URL;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class UrlUtils {

    private final UriBuilderFactory uriFactory;

    public String expandPath(URL url, String... paramters) {
        return uriFactory.builder().path(url.getPath()).build((Object[]) paramters).toString();
    }

    public static String createQueryString(String parameterName, String parameterValue, String existingQuery) {
        if (isNull(parameterValue)) {
            return existingQuery;
        }
        var paramSeparator = (isNull(existingQuery) || "".equals(existingQuery)) ? "" : "&";
        return existingQuery + paramSeparator + parameterName + "=" + parameterValue;
    }
}
