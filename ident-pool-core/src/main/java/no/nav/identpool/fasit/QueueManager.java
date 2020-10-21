package no.nav.identpool.fasit;

import lombok.Value;

@Value
public class QueueManager implements FasitResource {

    private String name;
    private String hostname;
    private String port;
}