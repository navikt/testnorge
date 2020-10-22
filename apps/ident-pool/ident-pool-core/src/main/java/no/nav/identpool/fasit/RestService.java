package no.nav.identpool.fasit;

import lombok.Value;

@Value
public class RestService implements FasitResource {

    private String endpointUrl;
}