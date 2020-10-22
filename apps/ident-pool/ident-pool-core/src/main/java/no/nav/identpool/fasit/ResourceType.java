package no.nav.identpool.fasit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResourceType {
    WEB_SERVICE("WebserviceEndpoint"),
    REST_SERVICE("RestService"),
    DATASOURCE("DataSource"),
    CREDENTIAL("Credential"),
    BASE_URL("BaseUrl"),
    OPEN_ID_CONNECT("OpenIdConnect"),
    CERTIFICATE("Certificate"),
    QUEUE_MANAGER("QueueManager"),
    QUEUE("Queue"),
    LDAP("Ldap"),
    CICS("Cics");

    private String fasitType;
}