package no.nav.identpool.fasit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RegexUtils {

    static final Pattern URL_PATTERN = Pattern.compile("\"url\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern ENDPOINT_URL_PATTERN = Pattern.compile("\"endpointUrl\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern JWKS_URL_PATTERN = Pattern.compile("\"jwksUrl\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern ISSUER_URL_PATTERN = Pattern.compile("\"issuerUrl\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern AGENT_NAME_PATTERN = Pattern.compile("\"agentName\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern HOST_URL_PATTERN = Pattern.compile("\"hostUrl\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern USERNAME_PATTERN = Pattern.compile("\"username\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern QUEUE_NAME_PATTERN = Pattern.compile("\"queueName\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern PASSWORD_URL_PATTERN = Pattern.compile("\"ref\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern HOSTNAME_PATTERN = Pattern.compile("\"hostname\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern NAME_PATTERN = Pattern.compile("\"name\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern PORT_PATTERN = Pattern.compile("\"port\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern ALIAS_PATTERN = Pattern.compile("\"keystorealias\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern SERVICEUSER_BASEDN_PATTERN = Pattern.compile("\"serviceuser.basedn\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern USER_BASEDN_PATTERN = Pattern.compile("\"user.basedn\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern BASEDN_PATTERN = Pattern.compile("\"basedn\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern DOMAIN_PATTERN = Pattern.compile("\"domain\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    static final Pattern KEYSTORE_PASSWORD_PATTERN = Pattern.compile("\"secrets\": [^}]*\"ref\": \"(?<value>.*?)\"", Pattern.DOTALL);
    static final Pattern KEYSTORE_FILE_PATTERN = Pattern.compile("\"keystore\": [^}]*\"ref\": \"(?<value>.*?)\"", Pattern.DOTALL);
    static final Pattern CICSNAME_PATTERN = Pattern.compile("\"cicsname\": \"(?<value>.*?)\"", Pattern.MULTILINE);

    static String getValue(String json, Pattern pattern) {
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group("value");
        }
        throw new IllegalArgumentException("Failed to find value on json");
    }

    public static List<String> getValues(String json, Pattern pattern) {
        Matcher matcher = pattern.matcher(json);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group("value"));
        }
        return matches;
    }
}