package no.nav.registre.sdForvalter.consumer.rs;

import java.util.Set;

public interface ConsumerInitializer {

    void send(Set<Object> data, String environment);

}
