package no.nav.registre.testnorge.identservice.testdata.url;

import lombok.Getter;

@Getter
public enum FasitUrl {

    APPLICATIONS_V2_GET("%s/api/v2/applications"),
    APPLICATIONINSTANCES_V2_GET("%s/api/v2/applicationinstances"),
    RESOURCES_V2_GET("%s/api/v2/resources"),
    SCOPED_RESOURCE_V2_GET("%s/api/v2/scopedresource");

    private final String url;

    FasitUrl(String appUrl) {
        url = appUrl;
    }

    public static String createQueryPatternByParamName(String... paramNames) {
        StringBuilder queryStr = new StringBuilder();
        for (int i = 0; i < paramNames.length; i++) {
            queryStr
                    .append(i == 0 ? '?' : '&')
                    .append(paramNames[i])
                    .append("=%s");
        }
        return queryStr.toString();
    }
}

